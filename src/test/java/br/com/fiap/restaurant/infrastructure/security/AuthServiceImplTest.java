package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.application.dto.request.LoginRequest;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;
    @Mock JwtService jwtService;
    @InjectMocks AuthServiceImpl service;

    @Test void authenticatesAndReturnsBearerToken() {
        User user = User.builder().login("jane").build();
        when(userDetailsService.loadUserByUsername("jane")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-value");
        when(jwtService.getExpirationMs()).thenReturn(60000L);
        var response = service.login(new LoginRequest("jane", "password"));
        assertEquals("jwt-value", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(60000L, response.expiresInMs());
        verify(authenticationManager).authenticate(any());
    }

    @Test void translatesAuthenticationFailure() {
        doThrow(new BadCredentialsException("bad credentials")).when(authenticationManager).authenticate(any());
        var error = assertThrows(InvalidCredentialsException.class, () -> service.login(new LoginRequest("jane", "wrong")));
        assertEquals("Invalid login or password", error.getMessage());
        verifyNoInteractions(userDetailsService, jwtService);
    }
}
