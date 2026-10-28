package br.com.fiap.restaurant.infrastructure.security.jwt;

import br.com.fiap.restaurant.infrastructure.security.AuthenticatedUser;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService service = new JwtService(Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes()), 60000);

    @Test void createsAndValidatesTokenForSubject() {
        String token = service.generateToken(Fixtures.user(4L, "jane"));
        assertEquals("jane", service.extractUsername(token));
        assertTrue(service.isTokenValid(token, new AuthenticatedUser(Fixtures.user(4L, "jane"))));
        assertFalse(service.isTokenValid(token, new AuthenticatedUser(Fixtures.user(5L, "other"))));
        assertEquals(60000, service.getExpirationMs());
    }

    @Test void rejectsTamperedToken() {
        assertThrows(RuntimeException.class, () -> service.extractUsername("not-a-jwt"));
    }
}
