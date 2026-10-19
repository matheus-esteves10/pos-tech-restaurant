package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.application.dto.response.RestaurantEmployeeResponse;
import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.entity.User;

import java.util.List;

public interface RestaurantService {

    Restaurant findRestaurantById(Long restaurantId);
    List<RestaurantEmployeeResponse> getRestaurantEmployees(Long restaurantId, User authenticatedUser);
}
