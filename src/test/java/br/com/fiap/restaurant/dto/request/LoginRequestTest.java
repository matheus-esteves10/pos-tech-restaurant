package br.com.fiap.restaurant.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginRequestTest {

    @Test
    void testLoginRequestCreation() {
        LoginRequest request = new LoginRequest("joao123", "senha123");

        assertNotNull(request);
        assertEquals("joao123", request.login());
        assertEquals("senha123", request.password());
    }

    @Test
    void testLoginRequestWithDifferentCredentials() {
        LoginRequest request = new LoginRequest("admin", "adminpassword");

        assertEquals("admin", request.login());
        assertEquals("adminpassword", request.password());
    }

    @Test
    void testLoginRequestWithSpecialCharacters() {
        LoginRequest request = new LoginRequest("user@123", "p@ssw0rd!#$");

        assertEquals("user@123", request.login());
        assertEquals("p@ssw0rd!#$", request.password());
    }
}
