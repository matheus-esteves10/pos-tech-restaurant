package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class CreateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    public CreateRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public RestaurantDetails execute(CreateRestaurantCommand command, User owner) {
        Restaurant restaurant = Restaurant.create(
                command.name(),
                command.cnpj(),
                command.description(),
                command.address(),
                command.phone(),
                command.kitchenType(),
                command.openingAt(),
                command.closingAt(),
                owner
        );
        return new RestaurantDetails(restaurantRepository.save(restaurant), List.of());
    }
}
