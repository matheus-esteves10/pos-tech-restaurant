package br.com.fiap.restaurant.infrastructure.persistence.jpa;

import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {

    Optional<ItemEntity> findByIdAndRestaurantId(Long id, Long restaurantId);

    List<ItemEntity> findAllByRestaurantId(Long restaurantId);
}
