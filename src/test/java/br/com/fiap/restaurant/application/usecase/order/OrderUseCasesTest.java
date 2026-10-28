package br.com.fiap.restaurant.application.usecase.order;

import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.domain.exception.InvalidOrderStatusException;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCasesTest {

    @Mock OrderRepository orderRepository;
    @Mock RestaurantRepository restaurantRepository;
    @Mock ItemRepository itemRepository;

    private final User owner = Fixtures.user(1L, "owner");
    private final User customer = Fixtures.user(4L, "customer");

    @Test
    void createsOrderWithRestaurantItems() {
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(itemRepository.findByIdAndRestaurantId(1L, 10L)).thenReturn(Optional.of(Fixtures.item(1L, 10L)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = new CreateOrderUseCase(orderRepository, restaurantRepository, itemRepository)
                .execute(10L, List.of(1L), customer);

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        assertEquals(4L, order.getUserId());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void rejectsOrderForUnknownRestaurantOrItem() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(itemRepository.findByIdAndRestaurantId(7L, 10L)).thenReturn(Optional.empty());
        CreateOrderUseCase useCase = new CreateOrderUseCase(orderRepository, restaurantRepository, itemRepository);

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(99L, List.of(1L), customer));
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(10L, List.of(7L), customer));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelsAndDeliversOrders() {
        Order toCancel = Fixtures.order(1L, 10L, 4L, OrderStatus.IN_PROGRESS);
        Order toDeliver = Fixtures.order(2L, 10L, 4L, OrderStatus.IN_PROGRESS);
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(orderRepository.findByIdAndRestaurantId(1L, 10L)).thenReturn(Optional.of(toCancel));
        when(orderRepository.findByIdAndRestaurantId(2L, 10L)).thenReturn(Optional.of(toDeliver));

        new CancelOrderUseCase(orderRepository, restaurantRepository).execute(10L, 1L, owner);
        new DeliverOrderUseCase(orderRepository, restaurantRepository).execute(10L, 2L, owner);

        assertEquals(OrderStatus.CANCELED, toCancel.getStatus());
        assertEquals(OrderStatus.DELIVERED, toDeliver.getStatus());
        verify(orderRepository).save(toCancel);
        verify(orderRepository).save(toDeliver);
        assertThrows(InvalidOrderStatusException.class,
                () -> new DeliverOrderUseCase(orderRepository, restaurantRepository).execute(10L, 1L, owner));
    }

    @Test
    void orderManagementRequiresMembershipAndExistingOrder() {
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(orderRepository.findByIdAndRestaurantId(5L, 10L)).thenReturn(Optional.empty());
        CancelOrderUseCase cancel = new CancelOrderUseCase(orderRepository, restaurantRepository);
        DeliverOrderUseCase deliver = new DeliverOrderUseCase(orderRepository, restaurantRepository);
        ListRestaurantOrdersUseCase list = new ListRestaurantOrdersUseCase(orderRepository, restaurantRepository);

        assertThrows(ForbiddenOperationException.class, () -> cancel.execute(10L, 5L, customer));
        assertThrows(ForbiddenOperationException.class, () -> deliver.execute(10L, 5L, customer));
        assertThrows(ForbiddenOperationException.class, () -> list.execute(10L, customer));
        assertThrows(EntityNotFoundException.class, () -> cancel.execute(10L, 5L, owner));
        assertThrows(EntityNotFoundException.class, () -> deliver.execute(10L, 5L, owner));
    }

    @Test
    void listsRestaurantOrders() {
        List<Order> orders = List.of(Fixtures.order(1L, 10L, 4L, OrderStatus.IN_PROGRESS));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(orderRepository.findAllByRestaurantId(10L)).thenReturn(orders);

        assertEquals(orders, new ListRestaurantOrdersUseCase(orderRepository, restaurantRepository).execute(10L, owner));
    }
}
