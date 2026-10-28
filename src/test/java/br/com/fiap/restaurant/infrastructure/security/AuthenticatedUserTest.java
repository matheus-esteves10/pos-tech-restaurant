package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserTest {

    @Test
    void exposesDomainUserAsUserDetails() {
        User user = Fixtures.user(1L, "jane");
        AuthenticatedUser principal = new AuthenticatedUser(user);

        assertEquals("jane", principal.getUsername());
        assertEquals("hash", principal.getPassword());
        assertTrue(principal.getAuthorities().isEmpty());
        assertTrue(principal.isEnabled());

        user.disable();
        assertFalse(principal.isEnabled());
    }

    @Test
    void passwordGatewayDelegatesToEncoder() {
        BCryptPasswordEncoderGateway gateway = new BCryptPasswordEncoderGateway(new BCryptPasswordEncoder());

        String encoded = gateway.encode("strong-password");

        assertNotEquals("strong-password", encoded);
        assertTrue(gateway.matches("strong-password", encoded));
        assertFalse(gateway.matches("wrong", encoded));
    }
}
