package com.stridehub.payment.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {

    private static final String WEBHOOK_SECRET = "mock-webhook-secret";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("delete from inventory_reservations");
        jdbcTemplate.execute("delete from refunds");
        jdbcTemplate.execute("delete from reviews");
        jdbcTemplate.execute("delete from wishlist_items");
        jdbcTemplate.execute("delete from notifications");
        jdbcTemplate.execute("delete from audit_logs");
        jdbcTemplate.execute("delete from outbox_events");
        jdbcTemplate.execute("delete from order_items");
        jdbcTemplate.execute("delete from shipments");
        jdbcTemplate.execute("delete from orders");
        jdbcTemplate.execute("delete from payment_attempts");
        jdbcTemplate.execute("delete from payments");
        jdbcTemplate.execute("delete from checkout_sessions");
        jdbcTemplate.execute("delete from cart_items");
        jdbcTemplate.execute("delete from carts");
        jdbcTemplate.execute("delete from inventory_items");
        jdbcTemplate.execute("delete from product_images");
        jdbcTemplate.execute("delete from product_variants");
        jdbcTemplate.execute("delete from products");
        jdbcTemplate.execute("delete from categories");
        jdbcTemplate.execute("delete from brands");
        jdbcTemplate.execute("delete from seller_profiles");
        jdbcTemplate.execute("delete from seller_applications");
        jdbcTemplate.execute("delete from refresh_tokens");
        jdbcTemplate.execute("delete from addresses");
        jdbcTemplate.execute("delete from user_roles");
        jdbcTemplate.execute("delete from users");
    }

    @Test
    void shouldCreatePaymentIntentForActiveCheckoutSession() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("145.00"), 5);
        String accessToken = registerAndLogin("payment-intent@example.com");
        addItemToCart(accessToken, variantId, 2);
        UUID checkoutSessionId = createCheckoutSession(accessToken);

        MvcResult result = mockMvc.perform(post("/api/v1/checkout/payment-intent")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("checkoutSessionId", checkoutSessionId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").isString())
                .andExpect(jsonPath("$.providerReference").isString())
                .andExpect(header().exists("X-Correlation-Id"))
                .andReturn();

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        UUID paymentId = UUID.fromString(responseBody.get("paymentId").asText());

        String paymentStatus = jdbcTemplate.queryForObject(
                "select status from payments where id = ?",
                String.class,
                paymentId
        );
        Integer attemptCount = jdbcTemplate.queryForObject(
                "select count(*) from payment_attempts where payment_id = ?",
                Integer.class,
                paymentId
        );

        assertThat(paymentStatus).isEqualTo("REQUIRES_ACTION");
        assertThat(attemptCount).isEqualTo(1);
    }

    @Test
    void shouldRejectPaymentWebhookWithInvalidSignature() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("145.00"), 5);
        String accessToken = registerAndLogin("payment-invalid-signature@example.com");
        addItemToCart(accessToken, variantId, 1);
        UUID checkoutSessionId = createCheckoutSession(accessToken);
        String providerReference = createPaymentIntent(accessToken, checkoutSessionId);

        String payload = objectMapper.writeValueAsString(Map.of(
                "eventId", "evt-invalid-signature",
                "providerReference", providerReference,
                "eventType", "payment.captured",
                "amount", 145.00,
                "currency", "USD"
        ));

        mockMvc.perform(post("/api/v1/webhooks/payment/provider")
                        .header("X-Signature", "invalid")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("payment.invalid_signature"));
    }

    @Test
    void shouldProcessCapturedWebhookOnlyOnceWhenReplayed() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("145.00"), 5);
        String accessToken = registerAndLogin("payment-webhook@example.com");
        addItemToCart(accessToken, variantId, 1);
        UUID checkoutSessionId = createCheckoutSession(accessToken);
        String providerReference = createPaymentIntent(accessToken, checkoutSessionId);

        String payload = objectMapper.writeValueAsString(Map.of(
                "eventId", "evt-captured-1",
                "providerReference", providerReference,
                "eventType", "payment.captured",
                "amount", 145.00,
                "currency", "USD"
        ));
        String signature = signPayload(payload);

        mockMvc.perform(post("/api/v1/webhooks/payment/provider")
                        .header("X-Signature", signature)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/api/v1/webhooks/payment/provider")
                        .header("X-Signature", signature)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isAccepted());

        String paymentStatus = jdbcTemplate.queryForObject(
                "select status from payments where provider_payment_ref = ?",
                String.class,
                providerReference
        );
        Integer webhookAttemptCount = jdbcTemplate.queryForObject(
                "select count(*) from payment_attempts where provider_request_id = ?",
                Integer.class,
                "evt-captured-1"
        );

        assertThat(paymentStatus).isEqualTo("CAPTURED");
        assertThat(webhookAttemptCount).isEqualTo(1);
    }

    private UUID insertCatalogGraph(BigDecimal priceAmount, int availableQuantity) {
        UUID categoryId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcTemplate.update(
                "insert into categories (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                categoryId,
                "Sneakers",
                "sneakers",
                "Sneakers",
                true,
                now,
                now
        );
        jdbcTemplate.update(
                "insert into brands (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                brandId,
                "StrideLab",
                "stridelab",
                "StrideLab",
                true,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into products (
                    id, seller_id, category_id, brand_id, name, slug, short_description, long_description,
                    currency_code, status, published_at, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                productId,
                null,
                categoryId,
                brandId,
                "Cloud Walker",
                "cloud-walker-" + productId,
                "Cloud Walker",
                "Cloud Walker Long",
                "USD",
                "ACTIVE",
                now,
                0L,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into product_variants (
                    id, product_id, sku, size_value, color_value, price_amount, compare_at_amount,
                    status, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                variantId,
                productId,
                "CW-" + variantId,
                "42",
                "Blue",
                priceAmount,
                null,
                "ACTIVE",
                0L,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into inventory_items (
                    id, variant_id, available_quantity, reserved_quantity, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                inventoryId,
                variantId,
                availableQuantity,
                0,
                0L,
                now,
                now
        );
        return variantId;
    }

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Buyer Example",
                                "email", email,
                                "password", "Password123!"
                        ))))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/identity/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", "Password123!"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }

    private void addItemToCart(String accessToken, UUID variantId, int quantity) throws Exception {
        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "variantId", variantId,
                                "quantity", quantity
                        ))))
                .andExpect(status().isOk());
    }

    private UUID createCheckoutSession(String accessToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/checkout/sessions")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "shippingAddressId", UUID.randomUUID()
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(body.get("id").asText());
    }

    private String createPaymentIntent(String accessToken, UUID checkoutSessionId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/checkout/payment-intent")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("checkoutSessionId", checkoutSessionId))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("providerReference").asText();
    }

    private String signPayload(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
