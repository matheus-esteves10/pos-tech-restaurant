package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateOrderRequest;
import br.com.fiap.restaurant.dto.response.OrderResponse;
import br.com.fiap.restaurant.exception.EntityNotFoundException;
import br.com.fiap.restaurant.exception.InvalidOrderStatusException;
import br.com.fiap.restaurant.model.Item;
import br.com.fiap.restaurant.model.Order;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.OrderStatus;
import br.com.fiap.restaurant.repository.OrderRepository;
import br.com.fiap.restaurant.service.ItemService;
import br.com.fiap.restaurant.service.OrderService;
import br.com.fiap.restaurant.service.RestaurantService;
import br.com.fiap.restaurant.service.RestaurantUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final RestaurantUserService restaurantUserService;
    private final ItemService itemService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long restaurantId, User authenticatedUser) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        List<Item> items = request.itemIds().stream()
                .map(itemId -> itemService.findByIdAndRestaurantId(itemId, restaurantId))
                .toList();

        Order order = Order.builder()
                .restaurant(restaurant)
                .user(authenticatedUser)
                .items(items)
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.fromOrder(savedOrder);
    }

    @Transactional
    public void cancelOrder(Long restaurantId, Long orderId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        Order order = findByIdAndRestaurantId(orderId, restaurantId);

        validateOrderIsInProgress(order);

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Transactional
    public void deliverOrder(Long restaurantId, Long orderId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        Order order = findByIdAndRestaurantId(orderId, restaurantId);

        validateOrderIsInProgress(order);

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    @Transactional
    public List<OrderResponse> getOrdersByRestaurant(Long restaurantId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        restaurantService.findRestaurantById(restaurantId);

        return orderRepository.findAllByRestaurantId(restaurantId).stream()
                .map(OrderResponse::fromOrder)
                .toList();
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Order findByIdAndRestaurantId(Long orderId, Long restaurantId) {
        Order order = findById(orderId);

        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new EntityNotFoundException();
        }

        return order;
    }

    private void validateOrderIsInProgress(Order order) {
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException("Order already delivered");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException("Order already canceled");
        }
    }
}
