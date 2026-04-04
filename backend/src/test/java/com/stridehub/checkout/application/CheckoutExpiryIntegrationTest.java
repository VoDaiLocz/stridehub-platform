package com.stridehub.checkout.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stridehub.support.TestDatabaseCleaner;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
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
class CheckoutExpiryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CheckoutService checkoutService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetDatabase() {
        TestDatabaseCleaner.reset(jdbcTemplate);
    }

    @Test
    void shouldExpireOverdueCheckoutSessionsAndReleaseReservations() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("145.00"), 4);
        String accessToken = registerAndLogin("checkout-expiry@example.com");
        addItemToCart(accessToken, variantId, 2);
        UUID checkoutSessionId = createCheckoutSession(accessToken);

        jdbcTemplate.update(
                "update checkout_sessions set expires_at = ? where id = ?",
                Instant.now().minusSeconds(30),
                checkoutSessionId
        );

        checkoutService.expireExpiredSessions();

        String checkoutStatus = jdbcTemplate.queryForObject(
                "select status from checkout_sessions where id = ?",
                String.class,
                checkoutSessionId
        );
        String reservationStatus = jdbcTemplate.queryForObject(
                "select status from inventory_reservations where checkout_session_id = ?",
                String.class,
                checkoutSessionId
        );
        Integer reservedQuantity = jdbcTemplate.queryForObject(
                "select reserved_quantity from inventory_items where variant_id = ?",
                Integer.class,
                variantId
        );

        assertThat(checkoutStatus).isEqualTo("EXPIRED");
        assertThat(reservationStatus).isEqualTo("EXPIRED");
        assertThat(reservedQuantity).isZero();
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
}
