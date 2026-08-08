package br.com.fiap.restaurant.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMs
) {
}
