package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.model.RestaurantUser;
import br.com.fiap.restaurant.model.User;

public interface RestaurantUserService {

    void validateUserIsRestaurantOwner(Long restaurantId, User user);
    void validateUserIsAssocieted(User authenticatedUser, Long restaurantId);
    RestaurantUser getRestaurantUser(Long userId, Long restaurantId);
}
