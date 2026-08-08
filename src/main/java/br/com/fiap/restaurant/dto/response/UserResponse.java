package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.enums.UserType;

public record UserResponse(
        Long id,
        String name,
        String email,
        String login,
        String phone,
        UserType userType,
        Boolean enabled,
        AddressResponse address
) {
}
