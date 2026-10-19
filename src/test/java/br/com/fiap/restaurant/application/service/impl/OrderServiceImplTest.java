package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateOrderRequest;
import br.com.fiap.restaurant.application.service.ItemService;
import br.com.fiap.restaurant.application.service.RestaurantService;
import br.com.fiap.restaurant.application.service.RestaurantUserService;
import br.com.fiap.restaurant.domain.entity.Order;
import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.InvalidOrderStatusException;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private RestaurantUserService restaurantUserService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void createsOrderFromItemsInRestaurant() {
        User user = User.builder().id(4L).build();
        Restaurant restaurant = Restaurant.builder().id(2L).build();
        Order saved = Order.builder().id(9L).restaurant(restaurant).user(user).items(List.of()).build();
        when(restaurantService.findRestaurantById(2L)).thenReturn(restaurant);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        var response = service.createOrder(new CreateOrderRequest(List.of(6L)), 2L, user);
        assertEquals(9L, response.id());
        verify(itemService).findByIdAndRestaurantId(6L, 2L);
    }

    @Test
    void cancelsInProgressOrder() {
        Order order = order(OrderStatus.IN_PROGRESS, 2L);
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        service.cancelOrder(2L, 9L, User.builder().id(4L).build());
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void deliversInProgressOrder() {
        Order order = order(OrderStatus.IN_PROGRESS, 2L);
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        service.deliverOrder(2L, 9L, User.builder().id(4L).build());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void rejectsRepeatedTerminalTransitions() {
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order(OrderStatus.DELIVERED, 2L)));
        assertThrows(InvalidOrderStatusException.class, () -> service.cancelOrder(2L, 9L, User.builder().id(4L).build()));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order(OrderStatus.CANCELED, 2L)));
        assertThrows(InvalidOrderStatusException.class, () -> service.deliverOrder(2L, 10L, User.builder().id(4L).build()));
    }

    @Test
    void listsOrdersAfterMembershipValidation() {
        when(restaurantService.findRestaurantById(2L)).thenReturn(Restaurant.builder().id(2L).build());
        when(orderRepository.findAllByRestaurantId(2L)).thenReturn(List.of());
        assertTrue(service.getOrdersByRestaurant(2L, User.builder().id(4L).build()).isEmpty());
    }

    @Test
    void findsOrderById() {
        Order order = order(OrderStatus.IN_PROGRESS, 3L);
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        assertSame(order, service.findById(9L));
    }

    @Test
    void rejectsOrderFromAnotherRestaurant() {
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order(OrderStatus.IN_PROGRESS, 3L)));
        assertThrows(EntityNotFoundException.class, () -> service.cancelOrder(2L, 9L, User.builder().id(4L).build()));
    }

    @Test
    void rejectsOrderThatDoesNotExist() {
        when(orderRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.deliverOrder(2L, 10L, User.builder().id(4L).build()));
    }

    private Order order(OrderStatus status, Long restaurantId) {
        return Order.builder()
                .id(9L)
                .status(status)
                .restaurant(Restaurant.builder().id(restaurantId).build())
                .build();
    }
}
