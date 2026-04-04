package com.stridehub.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stridehub.support.TestDatabaseCleaner;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetCatalogData() {
        TestDatabaseCleaner.reset(jdbcTemplate);
    }

    @Test
    void shouldListActiveCatalogReferences() throws Exception {
        UUID categoryId = insertCategory("Sneakers", "sneakers", true);
        UUID brandId = insertBrand("StrideLab", "stridelab", true);
        insertActiveProductGraph(categoryId, brandId, "Cloud Walker", "cloud-walker", "42", "Blue", new BigDecimal("145.00"));

        mockMvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("sneakers"))
                .andExpect(jsonPath("$[0].name").value("Sneakers"))
                .andExpect(header().exists("X-Correlation-Id"));

        mockMvc.perform(get("/api/v1/catalog/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("stridelab"))
                .andExpect(jsonPath("$[0].name").value("StrideLab"));
    }

    @Test
    void shouldListOnlyActiveProductsAndSupportFilters() throws Exception {
        UUID sneakersId = insertCategory("Sneakers", "sneakers", true);
        UUID slidesId = insertCategory("Slides", "slides", true);
        UUID strideLabId = insertBrand("StrideLab", "stridelab", true);
        UUID northPassId = insertBrand("North Pass", "north-pass", true);

        insertActiveProductGraph(sneakersId, strideLabId, "Cloud Walker", "cloud-walker", "42", "Blue", new BigDecimal("145.00"));
        insertActiveProductGraph(slidesId, northPassId, "Cabin Slide", "cabin-slide", "40", "Sand", new BigDecimal("79.00"));
        insertInactiveProductGraph(sneakersId, strideLabId, "Archived Runner", "archived-runner");

        mockMvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[?(@.slug == 'archived-runner')]").isEmpty());

        mockMvc.perform(get("/api/v1/catalog/products")
                        .param("category", "sneakers")
                        .param("brand", "stridelab")
                        .param("size", "42")
                        .param("color", "Blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("cloud-walker"))
                .andExpect(jsonPath("$.items[0].brand.slug").value("stridelab"))
                .andExpect(jsonPath("$.items[0].category.slug").value("sneakers"))
                .andExpect(jsonPath("$.items[0].primaryVariant.size").value("42"))
                .andExpect(jsonPath("$.items[0].primaryVariant.color").value("Blue"));
    }

    @Test
    void shouldReturnProductDetailWithActiveVariantsOnly() throws Exception {
        UUID categoryId = insertCategory("Sneakers", "sneakers", true);
        UUID brandId = insertBrand("StrideLab", "stridelab", true);
        UUID productId = insertProduct(categoryId, brandId, "Cloud Walker", "cloud-walker", "ACTIVE");
        UUID activeVariantId = insertVariant(productId, "CW-BL-42", "42", "Blue", "ACTIVE", new BigDecimal("145.00"));
        insertVariant(productId, "CW-BL-43", "43", "Blue", "ARCHIVED", new BigDecimal("145.00"));
        insertImage(productId, activeVariantId, "https://cdn.example.com/cloud-walker-blue.jpg", "Cloud Walker Blue", 0);
        insertImage(productId, null, "https://cdn.example.com/cloud-walker-editorial.jpg", "Cloud Walker Editorial", 1);

        mockMvc.perform(get("/api/v1/catalog/products/cloud-walker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("cloud-walker"))
                .andExpect(jsonPath("$.variants.length()").value(1))
                .andExpect(jsonPath("$.variants[0].sku").value("CW-BL-42"))
                .andExpect(jsonPath("$.variants[0].price").value(145.00))
                .andExpect(jsonPath("$.images.length()").value(2))
                .andExpect(jsonPath("$.images[0].imageUrl").value("https://cdn.example.com/cloud-walker-blue.jpg"));
    }

    @Test
    void shouldReturnNotFoundForInactiveProductDetail() throws Exception {
        UUID categoryId = insertCategory("Sneakers", "sneakers", true);
        UUID brandId = insertBrand("StrideLab", "stridelab", true);
        insertProduct(categoryId, brandId, "Archived Runner", "archived-runner", "ARCHIVED");

        mockMvc.perform(get("/api/v1/catalog/products/archived-runner"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("catalog.product_not_found"));
    }

    private UUID insertCategory(String name, String slug, boolean active) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into categories (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                id,
                name,
                slug,
                name + " description",
                active,
                Instant.now(),
                Instant.now()
        );
        return id;
    }

    private UUID insertBrand(String name, String slug, boolean active) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into brands (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                id,
                name,
                slug,
                name + " description",
                active,
                Instant.now(),
                Instant.now()
        );
        return id;
    }

    private UUID insertActiveProductGraph(
            UUID categoryId,
            UUID brandId,
            String productName,
            String productSlug,
            String size,
            String color,
            BigDecimal price
    ) {
        UUID productId = insertProduct(categoryId, brandId, productName, productSlug, "ACTIVE");
        UUID variantId = insertVariant(productId, productSlug.toUpperCase().replace('-', '_') + "_SKU", size, color, "ACTIVE", price);
        insertImage(productId, variantId, "https://cdn.example.com/" + productSlug + ".jpg", productName, 0);
        return productId;
    }

    private UUID insertInactiveProductGraph(UUID categoryId, UUID brandId, String name, String slug) {
        UUID productId = insertProduct(categoryId, brandId, name, slug, "ARCHIVED");
        insertVariant(productId, slug.toUpperCase().replace('-', '_') + "_SKU", "41", "Grey", "ACTIVE", new BigDecimal("120.00"));
        return productId;
    }

    private UUID insertProduct(UUID categoryId, UUID brandId, String name, String slug, String status) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        jdbcTemplate.update(
                """
                insert into products (
                    id, seller_id, category_id, brand_id, name, slug, short_description, long_description,
                    currency_code, status, published_at, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                null,
                categoryId,
                brandId,
                name,
                slug,
                name + " short description",
                name + " long description",
                "USD",
                status,
                now,
                0L,
                now,
                now
        );
        return id;
    }

    private UUID insertVariant(
            UUID productId,
            String sku,
            String size,
            String color,
            String status,
            BigDecimal price
    ) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        jdbcTemplate.update(
                """
                insert into product_variants (
                    id, product_id, sku, size_value, color_value, price_amount, compare_at_amount,
                    status, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                productId,
                sku,
                size,
                color,
                price,
                null,
                status,
                0L,
                now,
                now
        );
        return id;
    }

    private void insertImage(UUID productId, UUID variantId, String imageUrl, String altText, int sortOrder) {
        Instant now = Instant.now();
        jdbcTemplate.update(
                """
                insert into product_images (
                    id, product_id, variant_id, image_url, alt_text, sort_order, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID(),
                productId,
                variantId,
                imageUrl,
                altText,
                sortOrder,
                now,
                now
        );
    }
}
