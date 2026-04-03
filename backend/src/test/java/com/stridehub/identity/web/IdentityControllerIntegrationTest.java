package com.stridehub.identity.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
class IdentityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("delete from refresh_tokens");
        jdbcTemplate.execute("delete from addresses");
        jdbcTemplate.execute("delete from user_roles");
        jdbcTemplate.execute("delete from users");
    }

    @Test
    void shouldRegisterBuyerAndReturnTokens() throws Exception {
        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Avery Stone",
                                "email", "avery@example.com",
                                "password", "Password123!"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.user.email").value("avery@example.com"))
                .andExpect(jsonPath("$.user.name").value("Avery Stone"))
                .andExpect(jsonPath("$.user.roles[0]").value("BUYER"))
                .andExpect(header().exists("X-Correlation-Id"));

        Integer userCount = jdbcTemplate.queryForObject(
                "select count(*) from users where email = 'avery@example.com'",
                Integer.class
        );
        Integer refreshTokenCount = jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens",
                Integer.class
        );

        assertThat(userCount).isEqualTo(1);
        assertThat(refreshTokenCount).isEqualTo(1);
    }

    @Test
    void shouldRejectDuplicateRegistration() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "name", "Jordan Hale",
                "email", "jordan@example.com",
                "password", "Password123!"
        ));

        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("identity.email_conflict"));
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        register("Morgan Lee", "morgan@example.com", "Password123!");

        mockMvc.perform(post("/api/v1/identity/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "morgan@example.com",
                                "password", "Password123!"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.user.email").value("morgan@example.com"))
                .andExpect(jsonPath("$.user.roles[0]").value("BUYER"));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        register("Taylor Reed", "taylor@example.com", "Password123!");

        mockMvc.perform(post("/api/v1/identity/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "taylor@example.com",
                                "password", "WrongPassword123!"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("identity.invalid_credentials"));
    }

    @Test
    void shouldRotateRefreshTokensAndRejectReuse() throws Exception {
        register("Casey Blair", "casey@example.com", "Password123!");
        String initialRefreshToken = loginAndExtractRefreshToken("casey@example.com", "Password123!");

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/identity/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "refreshToken", initialRefreshToken
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn();

        JsonNode refreshBody = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String rotatedRefreshToken = refreshBody.get("refreshToken").asText();

        assertThat(rotatedRefreshToken).isNotEqualTo(initialRefreshToken);

        mockMvc.perform(post("/api/v1/identity/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "refreshToken", initialRefreshToken
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("identity.invalid_refresh_token"));
    }

    @Test
    void shouldRequireAuthenticationForCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/v1/identity/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void shouldReturnCurrentUserProfileForAuthenticatedToken() throws Exception {
        register("Harper Vale", "harper@example.com", "Password123!");
        String accessToken = loginAndExtractAccessToken("harper@example.com", "Password123!");

        mockMvc.perform(get("/api/v1/identity/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("harper@example.com"))
                .andExpect(jsonPath("$.name").value("Harper Vale"))
                .andExpect(jsonPath("$.roles[0]").value("BUYER"));
    }

    private void register(String name, String email, String password) throws Exception {
        mockMvc.perform(post("/api/v1/identity/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isCreated());
    }

    private String loginAndExtractAccessToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/identity/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("accessToken").asText();
    }

    private String loginAndExtractRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/identity/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("refreshToken").asText();
    }
}
