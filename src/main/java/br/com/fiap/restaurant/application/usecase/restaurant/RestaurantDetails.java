package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Restaurant;

import java.util.List;

public record RestaurantDetails(Restaurant restaurant, List<Item> items) {

    public RestaurantDetails {
        items = List.copyOf(items);
    }
}
