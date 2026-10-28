package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.exception.DomainValidationException;
import br.com.fiap.restaurant.domain.exception.InvalidOrderStatusException;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void createsInProgressOrder() {
        Order order = Order.create(5L, 2L, List.of(Fixtures.item(1L, 5L)));

        assertNull(order.getId());
        assertNull(order.getCreatedAt());
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        assertEquals(5L, order.getRestaurantId());
        assertEquals(2L, order.getUserId());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void rejectsOrderWithoutRequiredData() {
        List<Item> items = List.of(Fixtures.item(1L, 5L));
        assertThrows(DomainValidationException.class, () -> Order.create(null, 2L, items));
        assertThrows(DomainValidationException.class, () -> Order.create(5L, null, items));
        assertThrows(DomainValidationException.class, () -> Order.create(5L, 2L, List.of()));
        assertThrows(DomainValidationException.class, () -> Order.create(5L, 2L, null));
    }

    @Test
    void cancelsAndDeliversOnlyInProgressOrders() {
        Order toCancel = Fixtures.order(1L, 5L, 2L, OrderStatus.IN_PROGRESS);
        toCancel.cancel();
        assertEquals(OrderStatus.CANCELED, toCancel.getStatus());
        assertEquals("Order already canceled",
                assertThrows(InvalidOrderStatusException.class, toCancel::deliver).getMessage());

        Order toDeliver = Fixtures.order(2L, 5L, 2L, OrderStatus.IN_PROGRESS);
        toDeliver.deliver();
        assertEquals(OrderStatus.DELIVERED, toDeliver.getStatus());
        assertEquals("Order already delivered",
                assertThrows(InvalidOrderStatusException.class, toDeliver::cancel).getMessage());
    }
}
