package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {
    @Test void configuresBcryptPasswordEncoder() {
        var config = new SecurityConfig(mock(JwtAuthenticationFilter.class),
                mock(JwtAuthenticationEntryPoint.class), mock(JwtAccessDeniedHandler.class));
        var encoder = config.passwordEncoder();
        String encoded = encoder.encode("strong-password");
        assertTrue(encoder.matches("strong-password", encoded));
        assertNotEquals("strong-password", encoded);
    }
}
