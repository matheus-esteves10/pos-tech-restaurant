package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.model.Address;

import java.time.LocalTime;

public record CreateRestaurantCommand(String name,
                                      String cnpj,
                                      String description,
                                      String phone,
                                      KitchenType kitchenType,
                                      LocalTime openingAt,
                                      LocalTime closingAt,
                                      Address address) {
}
