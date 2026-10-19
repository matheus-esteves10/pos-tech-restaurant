package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {
    @Test void configuresPasswordEncoderAndDaoProvider() {
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        var config = new SecurityConfig(userDetailsService, mock(JwtAuthenticationFilter.class),
                mock(JwtAuthenticationEntryPoint.class), mock(JwtAccessDeniedHandler.class));
        var encoder = config.passwordEncoder();
        String encoded = encoder.encode("strong-password");
        assertTrue(encoder.matches("strong-password", encoded));
        assertNotEquals("strong-password", encoded);
        var provider = config.authenticationProvider();
        assertNotNull(provider);
    }
}
