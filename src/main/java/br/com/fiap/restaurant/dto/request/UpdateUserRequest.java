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

        @Valid
        AddressRequest address
) {
}
