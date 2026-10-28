package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.domain.model.Address;
import br.com.fiap.restaurant.presentation.dto.response.AddressResponse;

public final class AddressPresenter {

    private AddressPresenter() {
    }

    public static AddressResponse toResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.street(),
                address.number(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.zipCode(),
                address.complement()
        );
    }
}
