package br.com.fiap.restaurant.dto.response;

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
