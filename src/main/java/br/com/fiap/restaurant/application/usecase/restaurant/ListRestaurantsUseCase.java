package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class ListRestaurantsUseCase {

    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;

    public ListRestaurantsUseCase(RestaurantRepository restaurantRepository, ItemRepository itemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.itemRepository = itemRepository;
    }

    public List<RestaurantDetails> execute() {
        return restaurantRepository.findAllActive().stream()
                .map(restaurant -> new RestaurantDetails(restaurant,
                        itemRepository.findAllActiveByRestaurantId(restaurant.getId())))
                .toList();
    }
}
