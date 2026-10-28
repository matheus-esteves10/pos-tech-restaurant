package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByIdAndRestaurantId(Long orderId, Long restaurantId);

    List<Order> findAllByRestaurantId(Long restaurantId);
}
