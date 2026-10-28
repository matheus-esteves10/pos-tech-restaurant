package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class CreateItemUseCase {

    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;

    public CreateItemUseCase(ItemRepository itemRepository, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Item execute(Long restaurantId, ItemCommand command, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.ensureMember(authenticatedUser);

        Item item = Item.create(
                restaurant.getId(),
                command.name(),
                command.description(),
                command.value(),
                command.imageUrl(),
                command.itemType()
        );
        return itemRepository.save(item);
    }
}
