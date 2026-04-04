package com.stridehub.seller.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stridehub.support.TestDatabaseCleaner;
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
class SellerApplicationControllerIntegrationTest {

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
    void shouldSubmitSellerApplicationForAuthenticatedBuyer() throws Exception {
        String accessToken = registerAndLogin("seller-applicant@example.com");

        mockMvc.perform(post("/api/v1/seller/application")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "storeName", "Stride Studio",
                                "legalName", "Stride Studio LLC"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.storeName").value("Stride Studio"))
                .andExpect(jsonPath("$.legalName").value("Stride Studio LLC"))
                .andExpect(header().exists("X-Correlation-Id"));

        Integer applicationCount = jdbcTemplate.queryForObject(
                "select count(*) from seller_applications where store_name = 'Stride Studio'",
                Integer.class
        );

        assertThat(applicationCount).isEqualTo(1);
    }

    @Test
    void shouldRejectDuplicateSellerApplicationForSameBuyer() throws Exception {
        String accessToken = registerAndLogin("seller-duplicate@example.com");
        String payload = objectMapper.writeValueAsString(Map.of(
                "storeName", "Stride Lab",
                "legalName", "Stride Lab LLC"
        ));

        mockMvc.perform(post("/api/v1/seller/application")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/seller/application")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("seller.application_conflict"));
    }

    @Test
    void shouldReturnCurrentSellerApplication() throws Exception {
        String accessToken = registerAndLogin("seller-status@example.com");

        mockMvc.perform(post("/api/v1/seller/application")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "storeName", "Stride Status",
                                "legalName", "Stride Status LLC"
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/seller/application")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.storeName").value("Stride Status"))
                .andExpect(jsonPath("$.legalName").value("Stride Status LLC"));
    }

    @Test
    void shouldRequireAuthenticationForSellerApplicationEndpoints() throws Exception {
        mockMvc.perform(post("/api/v1/seller/application")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "storeName", "Blocked Store",
                                "legalName", "Blocked Store LLC"
                        ))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/seller/application"))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Seller Candidate",
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
}
