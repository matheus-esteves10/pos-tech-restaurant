package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.model.Restaurant;

public interface RestaurantService {

    Restaurant findRestaurantById(Long restaurantId);
}
