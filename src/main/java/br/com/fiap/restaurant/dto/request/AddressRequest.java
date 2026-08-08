package br.com.fiap.restaurant.dto.request;

public record AddressRequest(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String complement
) {
}
