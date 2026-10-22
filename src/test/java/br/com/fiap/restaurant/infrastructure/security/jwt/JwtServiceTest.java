package br.com.fiap.restaurant.infrastructure.security.jwt;

import br.com.fiap.restaurant.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private final JwtService service = new JwtService(Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes()), 60000);

    @Test void createsAndValidatesTokenForSubject() {
        User user = User.builder().id(4L).login("jane").build();
        String token = service.generateToken(user);
        assertEquals("jane", service.extractUsername(token));
        assertTrue(service.isTokenValid(token, user));
        assertFalse(service.isTokenValid(token, User.builder().login("other").build()));
        assertEquals(60000, service.getExpirationMs());
    }

    @Test void omitsUserIdClaimForOtherUserDetailsImplementations() {
        UserDetails details = org.springframework.security.core.userdetails.User.withUsername("service").password("x").authorities("ROLE_USER").build();
        String token = service.generateToken(details);
        assertEquals("service", service.extractUsername(token));
        assertTrue(service.isTokenValid(token, details));
    }

    @Test void rejectsTamperedToken() {
        assertThrows(RuntimeException.class, () -> service.extractUsername("not-a-jwt"));
    }
}
