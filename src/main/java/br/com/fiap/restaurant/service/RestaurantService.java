package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.dto.response.RestaurantEmployeeResponse;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.User;

import java.util.List;

public interface RestaurantService {

    Restaurant findRestaurantById(Long restaurantId);
    List<RestaurantEmployeeResponse> getRestaurantEmployees(Long restaurantId, User authenticatedUser);
}
