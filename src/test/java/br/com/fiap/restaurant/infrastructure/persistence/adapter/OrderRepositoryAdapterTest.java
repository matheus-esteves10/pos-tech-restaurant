package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.infrastructure.persistence.EntityFixtures;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.ItemJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.OrderJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    @Mock OrderJpaRepository orderJpaRepository;
    @Mock RestaurantJpaRepository restaurantJpaRepository;
    @Mock UserJpaRepository userJpaRepository;
    @Mock ItemJpaRepository itemJpaRepository;
    @InjectMocks OrderRepositoryAdapter adapter;

    private final UserEntity customer = EntityFixtures.user(4L, "customer");
    private final RestaurantEntity restaurant = EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner"));
    private final ItemEntity item = EntityFixtures.item(1L, restaurant);

    @Test
    void insertsOrderWithReferences() {
        when(restaurantJpaRepository.getReferenceById(10L)).thenReturn(restaurant);
        when(userJpaRepository.getReferenceById(4L)).thenReturn(customer);
        when(itemJpaRepository.getReferenceById(1L)).thenReturn(item);
        when(orderJpaRepository.saveAndFlush(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity entity = invocation.getArgument(0);
            entity.setId(5L);
            return entity;
        });

        Order saved = adapter.save(Order.create(10L, 4L, List.of(Fixtures.item(1L, 10L))));

        assertEquals(5L, saved.getId());
        assertEquals(OrderStatus.IN_PROGRESS, saved.getStatus());
        assertEquals(1, saved.getItems().size());
    }

    @Test
    void updatesStatusOfExistingOrderOrFailsWhenMissing() {
        OrderEntity existing = EntityFixtures.order(5L, restaurant, customer, item);
        when(orderJpaRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(orderJpaRepository.saveAndFlush(existing)).thenReturn(existing);

        Order saved = adapter.save(Fixtures.order(5L, 10L, 4L, OrderStatus.DELIVERED));

        assertEquals(OrderStatus.DELIVERED, saved.getStatus());
        assertEquals(OrderStatus.DELIVERED, existing.getStatus());

        when(orderJpaRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> adapter.save(Fixtures.order(6L, 10L, 4L, OrderStatus.CANCELED)));
    }

    @Test
    void findsOrdersOfRestaurant() {
        OrderEntity entity = EntityFixtures.order(5L, restaurant, customer, item);
        when(orderJpaRepository.findByIdAndRestaurantId(5L, 10L)).thenReturn(Optional.of(entity));
        when(orderJpaRepository.findAllByRestaurantId(10L)).thenReturn(List.of(entity));

        assertEquals(5L, adapter.findByIdAndRestaurantId(5L, 10L).orElseThrow().getId());
        assertEquals(1, adapter.findAllByRestaurantId(10L).size());
    }
}
