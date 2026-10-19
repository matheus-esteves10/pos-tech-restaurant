package br.com.fiap.restaurant.application.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs
) {
}
