package br.com.fiap.restaurant.presentation.dto.response;

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
                                 AddressResponse address,
                                 List<RestaurantOwnerResponse> owners,
                                 List<ItemResponse> items) {
}
