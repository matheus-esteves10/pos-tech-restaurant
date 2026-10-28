package br.com.fiap.restaurant.infrastructure.persistence.jpa;

import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Long> {
}
