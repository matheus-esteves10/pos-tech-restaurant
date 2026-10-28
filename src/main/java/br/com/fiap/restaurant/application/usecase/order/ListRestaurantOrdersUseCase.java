package br.com.fiap.restaurant.application.usecase.order;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class ListRestaurantOrdersUseCase {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public ListRestaurantOrdersUseCase(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<Order> execute(Long restaurantId, User authenticatedUser) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new)
                .ensureMember(authenticatedUser);

        return orderRepository.findAllByRestaurantId(restaurantId);
    }
}
