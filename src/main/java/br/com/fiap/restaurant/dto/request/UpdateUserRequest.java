package br.com.fiap.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,

        @Pattern(regexp = "\\d{11}", message = "Phone must contain 11 digits")
        String phone,

        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @Valid
        AddressRequest address
) {
}
