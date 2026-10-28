package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class UpdateItemUseCase {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public UpdateItemUseCase(ItemRepository itemRepository, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Item execute(Long restaurantId, Long itemId, ItemCommand command, User authenticatedUser) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new)
                .ensureMember(authenticatedUser);

        Item item = itemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        item.update(command.name(), command.description(), command.value(), command.imageUrl(), command.itemType());

        return itemRepository.save(item);
    }
}
