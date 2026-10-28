package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findActiveByIdAndRestaurantId(Long itemId, Long restaurantId);

    List<Item> findAllActiveByRestaurantId(Long restaurantId);
}
