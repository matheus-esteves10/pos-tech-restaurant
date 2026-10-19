package br.com.fiap.restaurant.application.dto.response;

import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.enums.KitchenType;

import java.time.LocalTime;
import java.util.List;

public record RestaurantResponse(Long id,
                                 String name,
                                 String cnpj,
                                 String description,
                                 String phone,
                                 KitchenType kitchenType,
                                 LocalTime openingAt,
                                 LocalTime closingAt,
                                 AddressResponse addressRequest,
                                 List<ItemResponse> items) {

    public static RestaurantResponse fromRestaurant(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                restaurant.getKitchenType(),
                restaurant.getOpeningAt(),
                restaurant.getClosingAt(),
                AddressResponse.fromAddress(restaurant.getAddress()),
                restaurant.getItems().stream()
                        .map(ItemResponse::fromItem)
                        .toList()
        );
    }
}
