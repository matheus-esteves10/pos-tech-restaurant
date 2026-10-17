package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.enums.KitchenType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

public record UpdateRestaurantRequest(String name,
                                      String description,
                                      @Valid AddressRequest address,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone,
                                      KitchenType kitchenType,
                                      LocalTime openingAt,
                                      LocalTime closingAt) {

    public static void update(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.name != null) {
            restaurant.setName(request.name);
        }
        if (request.description != null) {
            restaurant.setDescription(request.description);
        }
        if (request.address != null) {
            restaurant.setAddress(AddressRequest.toAddress(request.address));
        }
        if (request.phone != null) {
            restaurant.setPhone(request.phone);
        }
        if (request.kitchenType() != null) {
            restaurant.setKitchenType(request.kitchenType());
        }
        if (request.openingAt() != null) {
            restaurant.setOpeningAt(request.openingAt());
        }
        if (request.closingAt() != null) {
            restaurant.setClosingAt(request.closingAt());
        }
    }
}
