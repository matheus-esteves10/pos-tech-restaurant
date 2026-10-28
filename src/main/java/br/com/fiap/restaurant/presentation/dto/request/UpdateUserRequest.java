package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.user.UpdateUserCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
        @Email(message = "Email must be valid")
        String email,

        String login,

        @Pattern(regexp = "\\d{11}", message = "Phone must contain 11 digits")
        String phone,

        @Valid
        AddressRequest address
) {

    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(email, login, phone, AddressRequest.toAddress(address));
    }
}
