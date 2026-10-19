package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.domain.entity.Item;

public interface ItemService {

    Item findById(Long itemId);

    Item findByIdAndRestaurantId(Long itemId, Long restaurantId);
}
