package br.com.fiap.restaurant.repository;

import br.com.fiap.restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByRestaurantId(Long restaurantId);
}
