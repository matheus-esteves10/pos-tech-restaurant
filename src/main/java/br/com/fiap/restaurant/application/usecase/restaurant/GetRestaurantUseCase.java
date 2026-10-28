package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class GetRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;

    public GetRestaurantUseCase(RestaurantRepository restaurantRepository, ItemRepository itemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.itemRepository = itemRepository;
    }

    public RestaurantDetails execute(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
        return new RestaurantDetails(restaurant, itemRepository.findAllByRestaurantId(restaurantId));
    }
}
