package br.com.fiap.restaurant.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginResponseTest {

    @Test
    void testLoginResponseCreation() {
        LoginResponse response = new LoginResponse("token_value", "Bearer", 3600000);

        assertNotNull(response);
        assertEquals("token_value", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600000, response.expirationMs());
    }

    @Test
    void testLoginResponseWithDifferentExpiration() {
        LoginResponse response = new LoginResponse("token_value", "Bearer", 1800000);

        assertEquals(1800000, response.expirationMs());
    }

    @Test
    void testLoginResponseWithDifferentTokenType() {
        LoginResponse response = new LoginResponse("token_value", "JWT", 3600000);

        assertEquals("JWT", response.tokenType());
    }
}
