package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class UpdateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;

    public UpdateRestaurantUseCase(RestaurantRepository restaurantRepository, ItemRepository itemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.itemRepository = itemRepository;
    }

    public RestaurantDetails execute(Long restaurantId, UpdateRestaurantCommand command, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        restaurant.ensureOwner(authenticatedUser);
        restaurant.update(
                command.name(),
                command.description(),
                command.address(),
                command.phone(),
                command.kitchenType(),
                command.openingAt(),
                command.closingAt()
        );

        Restaurant saved = restaurantRepository.save(restaurant);
        return new RestaurantDetails(saved, itemRepository.findAllByRestaurantId(restaurantId));
    }
}
