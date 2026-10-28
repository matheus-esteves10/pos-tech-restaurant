package br.com.fiap.restaurant.application.usecase.order;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

@UseCase
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public CancelOrderUseCase(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void execute(Long restaurantId, Long orderId, User authenticatedUser) {
        restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new)
                .ensureMember(authenticatedUser);

        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        order.cancel();

        orderRepository.save(order);
    }
}
