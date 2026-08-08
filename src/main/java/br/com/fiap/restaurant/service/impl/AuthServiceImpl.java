package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.LoginRequest;
import br.com.fiap.restaurant.dto.response.LoginResponse;
import br.com.fiap.restaurant.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.security.jwt.JwtService;
import br.com.fiap.restaurant.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.login(), request.password())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.login());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs());
    }
}
