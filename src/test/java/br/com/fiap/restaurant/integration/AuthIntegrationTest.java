package br.com.fiap.restaurant.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestSupport {

    @Test
    void registersUserWithHashedPasswordAndIssuesBearerToken() throws Exception {
        register("jane");

        String storedPassword = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE login = 'jane'", String.class);
        assertThat(storedPassword).isNotEqualTo(PASSWORD).startsWith("$2");

        mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "jane", "password": "%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInMs").isNumber());
    }

    @Test
    void rejectsWrongPasswordAndUnknownLogin() throws Exception {
        register("jane");

        mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "jane", "password": "wrong-pass"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid login or password"));

        mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "ghost", "password": "whatever1"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidRegistrationPayload() throws Exception {
        mockMvc.perform(post(API + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Jo", "email": "not-an-email", "login": "jo", "password": "short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").value("Email must be valid"))
                .andExpect(jsonPath("$.validationErrors.password").exists());

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class)).isZero();
    }

    @Test
    void rejectsDuplicateLoginAndEmail() throws Exception {
        register("jane");

        mockMvc.perform(post(API + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Jane Again", "email": "jane@mail.com", "login": "jane2", "password": "%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email already in use"));
    }

    @Test
    void protectsEndpointsFromMissingOrInvalidTokens() throws Exception {
        mockMvc.perform(get(API + "/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get(API + "/restaurant").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void revokesAccessWhenUserDisablesAccount() throws Exception {
        Session jane = newUser("jane");

        deleteAs(jane, "/users/me").andExpect(status().isNoContent());

        getAs(jane, "/users").andExpect(status().isUnauthorized());
        mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "jane", "password": "%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isUnauthorized());
    }
}
