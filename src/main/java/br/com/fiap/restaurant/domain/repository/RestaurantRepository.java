package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findActiveById(Long id);

    List<Restaurant> findAllActive();
}
