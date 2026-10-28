package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.infrastructure.persistence.EntityFixtures;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.ItemJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryAdapterTest {

    @Mock ItemJpaRepository itemJpaRepository;
    @Mock RestaurantJpaRepository restaurantJpaRepository;
    @InjectMocks ItemRepositoryAdapter adapter;

    private final RestaurantEntity restaurant = EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner"));

    @Test
    void insertsItemLinkedToRestaurant() {
        when(restaurantJpaRepository.getReferenceById(10L)).thenReturn(restaurant);
        when(itemJpaRepository.saveAndFlush(any(ItemEntity.class))).thenAnswer(invocation -> {
            ItemEntity entity = invocation.getArgument(0);
            entity.setId(3L);
            return entity;
        });

        Item saved = adapter.save(Item.create(10L, "Pizza", null, BigDecimal.ONE, null, null));

        assertEquals(3L, saved.getId());
        assertEquals(10L, saved.getRestaurantId());
    }

    @Test
    void updatesExistingItemOrFailsWhenMissing() {
        ItemEntity existing = EntityFixtures.item(3L, restaurant);
        when(itemJpaRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(itemJpaRepository.saveAndFlush(existing)).thenReturn(existing);
        Item item = Fixtures.item(3L, 10L);
        item.disable();

        assertFalse(adapter.save(item).isEnabled());

        when(itemJpaRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adapter.save(Fixtures.item(4L, 10L)));
    }

    @Test
    void findsItemsOfRestaurant() {
        ItemEntity entity = EntityFixtures.item(3L, restaurant);
        when(itemJpaRepository.findByIdAndRestaurantIdAndEnabledTrue(3L, 10L)).thenReturn(Optional.of(entity));
        when(itemJpaRepository.findAllByRestaurantIdAndEnabledTrue(10L)).thenReturn(List.of(entity));

        assertEquals("Burger", adapter.findActiveByIdAndRestaurantId(3L, 10L).orElseThrow().getName());
        assertEquals(1, adapter.findAllActiveByRestaurantId(10L).size());
    }
}
