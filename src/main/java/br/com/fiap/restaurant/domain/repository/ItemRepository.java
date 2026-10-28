package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findByIdAndRestaurantId(Long itemId, Long restaurantId);

    List<Item> findAllByRestaurantId(Long restaurantId);
}
