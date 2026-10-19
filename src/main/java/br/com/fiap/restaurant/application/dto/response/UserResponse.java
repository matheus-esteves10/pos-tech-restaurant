package br.com.fiap.restaurant.application.dto.response;

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
