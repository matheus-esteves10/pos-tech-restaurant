package br.com.fiap.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email(message = "Email must be valid")
        String email,

        String login,

        @Pattern(regexp = "\\d{11}", message = "Phone must contain 11 digits")
        String phone,

        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @Valid
        AddressRequest address
) {
}
