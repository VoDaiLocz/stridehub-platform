package com.stridehub.cart.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetDatabase() {
        TestDatabaseCleaner.reset(jdbcTemplate);
    }

    @Test
    void shouldReturnCurrentBuyerCart() throws Exception {
        String accessToken = registerAndLogin("cartbuyer@example.com");

        mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.subtotalAmount").value(0))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void shouldAddCartItemAndCalculateSubtotal() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("145.00"));
        String accessToken = registerAndLogin("buyer-add@example.com");

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "variantId", variantId,
                                "quantity", 2
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].variantId").value(variantId.toString()))
                .andExpect(jsonPath("$.items[0].sku").isString())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPriceAmount").value(145.0))
                .andExpect(jsonPath("$.items[0].lineTotalAmount").value(290.0))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.subtotalAmount").value(290.0));
    }

    @Test
    void shouldUpdateCartItemQuantity() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("120.00"));
        String accessToken = registerAndLogin("buyer-update@example.com");
        UUID itemId = addItemAndExtractItemId(accessToken, variantId, 1);

        mockMvc.perform(patch("/api/v1/cart/items/{itemId}", itemId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(itemId.toString()))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.subtotalAmount").value(360.0))
                .andExpect(jsonPath("$.totalItems").value(3));
    }

    @Test
    void shouldRemoveCartItem() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("99.00"));
        String accessToken = registerAndLogin("buyer-remove@example.com");
        UUID itemId = addItemAndExtractItemId(accessToken, variantId, 1);

        mockMvc.perform(delete("/api/v1/cart/items/{itemId}", itemId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.subtotalAmount").value(0))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void shouldIsolateCartItemsByBuyerOwnership() throws Exception {
        UUID variantId = insertCatalogGraph(new BigDecimal("130.00"));
        String firstBuyerToken = registerAndLogin("owner-one@example.com");
        String secondBuyerToken = registerAndLogin("owner-two@example.com");
        UUID itemId = addItemAndExtractItemId(firstBuyerToken, variantId, 1);

        mockMvc.perform(patch("/api/v1/cart/items/{itemId}", itemId)
                        .header("Authorization", "Bearer " + secondBuyerToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 4))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("cart.item_not_found"));

        mockMvc.perform(delete("/api/v1/cart/items/{itemId}", itemId)
                        .header("Authorization", "Bearer " + secondBuyerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("cart.item_not_found"));

        Integer persistedQuantity = jdbcTemplate.queryForObject(
                "select quantity from cart_items where id = ?",
                Integer.class,
                itemId
        );
        assertThat(persistedQuantity).isEqualTo(1);
    }

    private UUID insertCatalogGraph(BigDecimal priceAmount) {
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
                10,
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

    private UUID addItemAndExtractItemId(String accessToken, UUID variantId, int quantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "variantId", variantId,
                                "quantity", quantity
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(body.get("items").get(0).get("id").asText());
    }
}
