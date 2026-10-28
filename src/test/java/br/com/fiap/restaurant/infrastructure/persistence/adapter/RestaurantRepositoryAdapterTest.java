package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.infrastructure.persistence.EntityFixtures;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantMemberEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantRepositoryAdapterTest {

    @Mock RestaurantJpaRepository restaurantJpaRepository;
    @Mock UserJpaRepository userJpaRepository;
    @InjectMocks RestaurantRepositoryAdapter adapter;

    private final User owner = Fixtures.user(1L, "owner");
    private final User employee = Fixtures.user(2L, "employee");

    @Test
    void insertsRestaurantAndOwnerMembership() {
        when(restaurantJpaRepository.saveAndFlush(any(RestaurantEntity.class))).thenAnswer(invocation -> {
            RestaurantEntity entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });
        when(userJpaRepository.getReferenceById(1L)).thenReturn(EntityFixtures.user(1L, "owner"));

        Restaurant saved = adapter.save(Restaurant.create("Cafe", "123", null, null, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));

        assertEquals(10L, saved.getId());
        assertTrue(saved.isOwner(1L));
        verify(restaurantJpaRepository).flush();
    }

    @Test
    void syncsMembersOfExistingRestaurant() {
        UserEntity ownerEntity = EntityFixtures.user(1L, "owner");
        UserEntity removedEntity = EntityFixtures.user(3L, "removed");
        RestaurantEntity entity = EntityFixtures.restaurant(10L, ownerEntity);
        entity.getMembers().add(new RestaurantMemberEntity(entity, removedEntity, UserType.RESTAURANT_EMPLOYEE));
        when(restaurantJpaRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(restaurantJpaRepository.saveAndFlush(entity)).thenReturn(entity);
        when(userJpaRepository.getReferenceById(2L)).thenReturn(EntityFixtures.user(2L, "employee"));

        Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);
        restaurant.promoteToOwner(2L);
        Restaurant saved = adapter.save(restaurant);

        assertEquals(2, entity.getMembers().size());
        assertTrue(saved.isOwner(1L));
        assertTrue(saved.isOwner(2L));
        assertFalse(saved.hasMember(3L));
        assertEquals("Cafe", entity.getName());
    }

    @Test
    void failsToSaveUnknownRestaurant() {
        when(restaurantJpaRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adapter.save(Fixtures.restaurant(10L, owner)));
    }

    @Test
    void findsRestaurants() {
        RestaurantEntity entity = EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner"));
        when(restaurantJpaRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(restaurantJpaRepository.findAll()).thenReturn(List.of(entity));

        assertEquals("Cafe", adapter.findById(10L).orElseThrow().getName());
        assertEquals(1, adapter.findAll().size());
    }
}
