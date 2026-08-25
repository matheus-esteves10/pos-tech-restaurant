package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.LoginRequest;
import br.com.fiap.restaurant.dto.response.LoginResponse;
import br.com.fiap.restaurant.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("joao123", "senha123");
        testUser = User.builder()
                .id(1L)
                .login("joao123")
                .name("João Silva")
                .email("joao@email.com")
                .userType(UserType.CLIENT)
                .enabled(true)
                .build();
    }

    @Test
    void testLoginSuccess() {
        String token = "valid_jwt_token";
        long expirationMs = 3600000;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword()));
        when(userDetailsService.loadUserByUsername("joao123")).thenReturn(testUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
        when(jwtService.getExpirationMs()).thenReturn(expirationMs);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(expirationMs, response.expirationMs());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername("joao123");
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    void testLoginWithInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testLoginWithInvalidUsername() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("User not found"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLoginWithDisabledUser() {
        User disabledUser = User.builder()
                .id(1L)
                .login("joao123")
                .name("João Silva")
                .enabled(false)
                .build();

        String token = "valid_jwt_token";
        long expirationMs = 3600000;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(disabledUser.getUsername(), disabledUser.getPassword()));
        when(userDetailsService.loadUserByUsername("joao123")).thenReturn(disabledUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
        when(jwtService.getExpirationMs()).thenReturn(expirationMs);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(token, response.token());
    }

    @Test
    void testLoginTokenExpiration() {
        String token = "valid_jwt_token";
        long expirationMs = 1800000;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword()));
        when(userDetailsService.loadUserByUsername("joao123")).thenReturn(testUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
        when(jwtService.getExpirationMs()).thenReturn(expirationMs);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(1800000, response.expirationMs());
    }
}
