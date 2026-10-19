package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByRestaurantId(Long restaurantId);
}
