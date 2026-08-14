package br.com.fiap.restaurant.dto.response;

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
