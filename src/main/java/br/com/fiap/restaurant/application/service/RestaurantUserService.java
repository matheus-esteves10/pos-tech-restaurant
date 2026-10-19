package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.domain.entity.RestaurantUser;
import br.com.fiap.restaurant.domain.entity.User;

public interface RestaurantUserService {

    void validateUserIsRestaurantOwner(Long restaurantId, User user);
    void validateUserIsAssocieted(User authenticatedUser, Long restaurantId);
    RestaurantUser getRestaurantUser(Long userId, Long restaurantId);
}
