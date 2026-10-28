package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.model.Address;

import java.time.LocalTime;

public record UpdateRestaurantCommand(String name,
                                      String description,
                                      Address address,
                                      String phone,
                                      KitchenType kitchenType,
                                      LocalTime openingAt,
                                      LocalTime closingAt) {
}
