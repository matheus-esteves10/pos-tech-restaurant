package br.com.fiap.restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserPasswordRequest(
        @NotNull
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password
) {
}
