package br.com.fiap.restaurant.presentation.dto.response;

import br.com.fiap.restaurant.domain.enums.UserType;

public record RestaurantEmployeeResponse(String name,
                                         String login,
                                         String phone,
                                         UserType role) {
}
