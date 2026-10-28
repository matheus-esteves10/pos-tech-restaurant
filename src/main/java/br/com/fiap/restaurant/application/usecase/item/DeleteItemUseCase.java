package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class DeleteItemUseCase {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public DeleteItemUseCase(ItemRepository itemRepository, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void execute(Long restaurantId, Long itemId, User authenticatedUser) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new)
                .ensureMember(authenticatedUser);

        Item item = itemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        item.disable();

        itemRepository.save(item);
    }
}
