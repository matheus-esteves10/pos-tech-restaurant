package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class SetRestaurantOwnerUseCase {

    private final RestaurantRepository restaurantRepository;

    public SetRestaurantOwnerUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void execute(Long restaurantId, Long userId, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.ensureOwner(authenticatedUser);

        restaurant.promoteToOwner(userId);

        restaurantRepository.save(restaurant);
    }
}
