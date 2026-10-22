package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.dto.request.CreateOrderRequest;
import br.com.fiap.restaurant.application.dto.response.OrderResponse;
import br.com.fiap.restaurant.application.service.impl.OrderServiceImpl;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock OrderServiceImpl service;
    @InjectMocks OrderController controller;

    @Test void delegatesOrderCreationAndListing() {
        User user = User.builder().id(1L).build();
        OrderResponse order = response();
        var request = new CreateOrderRequest(List.of(3L));
        when(service.createOrder(request, 2L, user)).thenReturn(order);
        when(service.getOrdersByRestaurant(2L, user)).thenReturn(List.of(order));
        assertEquals(HttpStatus.CREATED, controller.createOrder(2L, request, user).getStatusCode());
        assertEquals(List.of(order), controller.getOrders(2L, user).getBody());
    }

    @Test void delegatesOrderStatusTransitions() {
        User user = User.builder().id(1L).build();
        assertEquals(HttpStatus.NO_CONTENT, controller.cancelOrder(2L, 4L, user).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.deliverOrder(2L, 4L, user).getStatusCode());
        verify(service).cancelOrder(2L, 4L, user);
        verify(service).deliverOrder(2L, 4L, user);
    }

    private OrderResponse response() {
        return new OrderResponse(4L, OrderStatus.IN_PROGRESS, List.of(), 2L, 1L, null);
    }
}
