package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.enums.UserType;

public record RestaurantUserResponse(String restaurantName,
                                     String userLogin,
                                     UserType userType) {
}
