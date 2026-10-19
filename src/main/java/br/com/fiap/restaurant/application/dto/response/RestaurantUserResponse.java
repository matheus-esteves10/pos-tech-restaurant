package br.com.fiap.restaurant.application.dto.response;

import br.com.fiap.restaurant.domain.enums.UserType;

public record RestaurantUserResponse(String restaurantName,
                                     String userLogin,
                                     UserType userType) {
}
