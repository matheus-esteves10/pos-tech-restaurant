package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.Restaurant;

public record RestaurantResponse(String name,
                                 String cnpj,
                                 String description,
                                 String phone,
                                 AddressResponse addressRequest) {

    public static RestaurantResponse fromRestaurant(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                AddressResponse.fromAddress(restaurant.getAddress())
        );
    }
}
