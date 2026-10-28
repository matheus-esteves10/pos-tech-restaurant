package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;

public record RestaurantMembership(Restaurant restaurant, User user, UserType role) {
}
