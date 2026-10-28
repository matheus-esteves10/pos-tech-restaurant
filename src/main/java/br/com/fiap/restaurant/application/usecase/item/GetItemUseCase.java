package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class GetItemUseCase {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public GetItemUseCase(ItemRepository itemRepository, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Item execute(Long restaurantId, Long itemId) {
        restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        return itemRepository.findActiveByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
