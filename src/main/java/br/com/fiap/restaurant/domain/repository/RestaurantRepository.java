package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
