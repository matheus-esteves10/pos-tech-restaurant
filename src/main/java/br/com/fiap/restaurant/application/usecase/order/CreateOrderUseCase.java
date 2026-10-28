package br.com.fiap.restaurant.application.usecase.order;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;

    public CreateOrderUseCase(OrderRepository orderRepository, RestaurantRepository restaurantRepository,
                              ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.itemRepository = itemRepository;
    }

    public Order execute(Long restaurantId, List<Long> itemIds, User authenticatedUser) {
        restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        List<Item> items = itemIds.stream()
                .map(itemId -> itemRepository.findActiveByIdAndRestaurantId(itemId, restaurantId)
                        .orElseThrow(EntityNotFoundException::new))
                .toList();

        return orderRepository.save(Order.create(restaurantId, authenticatedUser.getId(), items));
    }
}
