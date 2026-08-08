package br.com.fiap.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login cannot be blank")
        String login,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
