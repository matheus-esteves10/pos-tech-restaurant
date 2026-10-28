package br.com.fiap.restaurant.infrastructure.persistence.jpa;

import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Long> {

    Optional<RestaurantEntity> findByIdAndEnabledTrue(Long id);

    List<RestaurantEntity> findAllByEnabledTrue();
}
