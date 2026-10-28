package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.domain.model.Address;

public record AddressRequest(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String complement
) {

    public static Address toAddress(AddressRequest request) {
        if (request == null) {
            return null;
        }
        return new Address(
                request.street(),
                request.number(),
                request.neighborhood(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.complement()
        );
    }
}
