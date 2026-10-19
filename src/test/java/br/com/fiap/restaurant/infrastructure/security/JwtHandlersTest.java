package br.com.fiap.restaurant.infrastructure.security;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class JwtHandlersTest {
    @Test void writesProblemDetailsForUnauthenticatedRequest() throws Exception {
        var response = new MockHttpServletResponse();
        new JwtAuthenticationEntryPoint(new ObjectMapper()).commence(new MockHttpServletRequest("GET", "/private"), response, new BadCredentialsException("no"));
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentType().contains("application/problem+json"));
        assertTrue(response.getContentAsString().contains("Authentication is required"));
    }

    @Test void writesProblemDetailsForForbiddenRequest() throws Exception {
        var response = new MockHttpServletResponse();
        new JwtAccessDeniedHandler(new ObjectMapper()).handle(new MockHttpServletRequest("GET", "/admin"), response, new AccessDeniedException("no"));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("You do not have permission"));
    }
}
