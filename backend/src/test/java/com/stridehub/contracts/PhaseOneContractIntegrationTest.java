package com.stridehub.contracts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PhaseOneContractIntegrationTest {

    private static final Set<String> REQUIRED_PATHS = Set.of(
            "/api/v1/system/ping",
            "/api/v1/identity/register",
            "/api/v1/identity/login",
            "/api/v1/identity/refresh",
            "/api/v1/identity/me",
            "/api/v1/catalog/categories",
            "/api/v1/catalog/brands",
            "/api/v1/catalog/products",
            "/api/v1/catalog/products/{slug}",
            "/api/v1/cart",
            "/api/v1/cart/items",
            "/api/v1/cart/items/{itemId}",
            "/api/v1/checkout/sessions",
            "/api/v1/checkout/payment-intent",
            "/api/v1/orders",
            "/api/v1/orders/{orderId}",
            "/api/v1/seller/application",
            "/api/v1/webhooks/payment/provider"
    );

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldExposeImplementedPhaseOnePathsInRuntimeOpenApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode paths = root.get("paths");

        assertThat(paths).isNotNull();
        for (String requiredPath : REQUIRED_PATHS) {
            assertThat(paths.has(requiredPath))
                    .as("runtime OpenAPI should expose %s", requiredPath)
                    .isTrue();
        }
    }

    @Test
    void shouldReturnSharedErrorEnvelopeForValidationFailures() throws Exception {
        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "invalid-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.correlationId").isString())
                .andExpect(jsonPath("$.path").value("/api/v1/identity/register"))
                .andExpect(header().exists("X-Correlation-Id"));
    }
}
