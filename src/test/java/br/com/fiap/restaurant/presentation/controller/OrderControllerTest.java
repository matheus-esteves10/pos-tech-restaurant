package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.usecase.order.CancelOrderUseCase;
import br.com.fiap.restaurant.application.usecase.order.CreateOrderUseCase;
import br.com.fiap.restaurant.application.usecase.order.DeliverOrderUseCase;
import br.com.fiap.restaurant.application.usecase.order.ListRestaurantOrdersUseCase;
import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.presentation.dto.request.CreateOrderRequest;
import br.com.fiap.restaurant.presentation.dto.response.OrderResponse;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock CreateOrderUseCase createOrderUseCase;
    @Mock CancelOrderUseCase cancelOrderUseCase;
    @Mock DeliverOrderUseCase deliverOrderUseCase;
    @Mock ListRestaurantOrdersUseCase listRestaurantOrdersUseCase;
    @InjectMocks OrderController controller;

    private final User user = Fixtures.user(4L, "customer");

    @Test
    void createsAndListsOrders() {
        when(createOrderUseCase.execute(10L, List.of(1L), user))
                .thenReturn(Fixtures.order(5L, 10L, 4L, OrderStatus.IN_PROGRESS));
        when(listRestaurantOrdersUseCase.execute(10L, user))
                .thenReturn(List.of(Fixtures.order(5L, 10L, 4L, OrderStatus.DELIVERED)));

        var created = controller.createOrder(10L, new CreateOrderRequest(List.of(1L)), user);
        OrderResponse body = created.getBody();

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals(5L, body.id());
        assertEquals(OrderStatus.IN_PROGRESS, body.status());
        assertEquals(10L, body.restaurantId());
        assertEquals(4L, body.userId());
        assertEquals(LocalDateTime.of(2026, 1, 1, 12, 0), body.timestamp());
        assertEquals(1, body.items().size());
        assertEquals(OrderStatus.DELIVERED, controller.getOrders(10L, user).getBody().getFirst().status());
    }

    @Test
    void cancelsAndDeliversOrders() {
        assertEquals(HttpStatus.NO_CONTENT, controller.cancelOrder(10L, 5L, user).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.deliverOrder(10L, 5L, user).getStatusCode());
        verify(cancelOrderUseCase).execute(10L, 5L, user);
        verify(deliverOrderUseCase).execute(10L, 5L, user);
    }
}
