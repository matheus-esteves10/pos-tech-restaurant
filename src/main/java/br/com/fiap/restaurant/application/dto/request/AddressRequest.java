package br.com.fiap.restaurant.application.dto.request;

import br.com.fiap.restaurant.domain.valueobject.Address;

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
        return Address.builder()
                .street(request.street())
                .number(request.number())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .complement(request.complement())
                .build();
    }
}
