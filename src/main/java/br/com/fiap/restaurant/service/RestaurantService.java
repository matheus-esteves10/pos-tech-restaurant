package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.model.User;

public interface RestaurantService {

    RestaurantResponse createRestaurant(CreateRestaurantRequest restaurantRequest, User authenticatedUser);
}
