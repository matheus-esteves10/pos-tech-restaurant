package br.com.fiap.restaurant.application.usecase.auth;

public record AuthToken(String token, String tokenType, long expiresInMs) {
}
