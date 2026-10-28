package br.com.fiap.restaurant.presentation.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs
) {
}
