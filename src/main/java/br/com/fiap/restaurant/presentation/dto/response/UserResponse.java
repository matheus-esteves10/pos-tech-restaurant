package br.com.fiap.restaurant.presentation.dto.response;

public record UserResponse(
        Long id,
        String name,
        String email,
        String login,
        String phone,
        Boolean enabled,
        AddressResponse address
) {
}
