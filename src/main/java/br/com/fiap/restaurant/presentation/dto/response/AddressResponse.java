package br.com.fiap.restaurant.presentation.dto.response;

public record AddressResponse(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String complement
) {
}
