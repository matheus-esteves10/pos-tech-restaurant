package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class ListRestaurantItemsUseCase {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public ListRestaurantItemsUseCase(ItemRepository itemRepository, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<Item> execute(Long restaurantId) {
        restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        return itemRepository.findAllActiveByRestaurantId(restaurantId);
    }
}
