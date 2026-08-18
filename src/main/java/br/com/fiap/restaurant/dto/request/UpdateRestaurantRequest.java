package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Restaurant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record UpdateRestaurantRequest(String name,
                                      String description,
                                      @Valid AddressRequest address,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone) {

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
    }
}
