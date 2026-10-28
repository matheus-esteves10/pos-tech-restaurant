package br.com.fiap.restaurant.infrastructure.security.jwt;

import br.com.fiap.restaurant.infrastructure.security.AuthenticatedUser;
import br.com.fiap.restaurant.support.Fixtures;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;
    @Mock FilterChain chain;
    @InjectMocks JwtAuthenticationFilter filter;

    @AfterEach void cleanSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test void continuesWithoutBearerHeader() throws Exception {
        filter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);
        verify(chain).doFilter(any(), any());
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test void authenticatesValidBearerToken() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        AuthenticatedUser principal = new AuthenticatedUser(Fixtures.user(1L, "jane"));
        when(jwtService.extractUsername("token")).thenReturn("jane");
        when(userDetailsService.loadUserByUsername("jane")).thenReturn(principal);
        when(jwtService.isTokenValid("token", principal)).thenReturn(true);
        filter.doFilterInternal(request, new MockHttpServletResponse(), chain);
        assertSame(principal, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain).doFilter(any(), any());
    }

    @Test void doesNotAuthenticateDisabledUser() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        var user = Fixtures.user(1L, "jane");
        user.disable();
        when(jwtService.extractUsername("token")).thenReturn("jane");
        when(userDetailsService.loadUserByUsername("jane")).thenReturn(new AuthenticatedUser(user));
        filter.doFilterInternal(request, new MockHttpServletResponse(), chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(chain).doFilter(any(), any());
    }

    @Test void clearsContextForInvalidTokenAndIgnoresNonBearerHeader() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");
        when(jwtService.extractUsername("invalid")).thenThrow(new io.jsonwebtoken.JwtException("invalid"));
        filter.doFilterInternal(request, new MockHttpServletResponse(), chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        var other = new MockHttpServletRequest();
        other.addHeader("Authorization", "Basic abc");
        filter.doFilterInternal(other, new MockHttpServletResponse(), chain);
        verify(chain, times(2)).doFilter(any(), any());
    }
}
