package br.com.fiap.restaurant.infrastructure.persistence.mapper;

import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.infrastructure.persistence.EntityFixtures;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceMapperTest {

    @Test
    void mapsUserBothWays() {
        UserEntity entity = EntityFixtures.user(1L, "jane");

        User user = PersistenceMapper.toDomain(entity);
        assertEquals(1L, user.getId());
        assertEquals("jane", user.getLogin());
        assertEquals("Main St", user.getAddress().street());
        assertTrue(user.isEnabled());

        user.disable();
        UserEntity target = new UserEntity();
        PersistenceMapper.copyToEntity(user, target);
        assertEquals("jane@mail.com", target.getEmail());
        assertEquals("hash", target.getPassword());
        assertEquals("Center", target.getAddress().getNeighborhood());
        assertFalse(target.getEnabled());
    }

    @Test
    void mapsRestaurantWithMembers() {
        RestaurantEntity entity = EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner"));

        Restaurant restaurant = PersistenceMapper.toDomain(entity);
        assertEquals(10L, restaurant.getId());
        assertEquals(UserType.RESTAURANT_OWNER, restaurant.getMembers().getFirst().getRole());
        assertNull(restaurant.getAddress());

        RestaurantEntity target = new RestaurantEntity();
        PersistenceMapper.copyToEntity(Fixtures.restaurant(10L, Fixtures.user(1L, "owner")), target);
        assertEquals("Cafe", target.getName());
        assertEquals("Sao Paulo", target.getAddress().getCity());
        assertTrue(target.getEnabled());
    }

    @Test
    void mapsItemBothWays() {
        ItemEntity entity = EntityFixtures.item(3L, EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner")));

        Item item = PersistenceMapper.toDomain(entity);
        assertEquals(3L, item.getId());
        assertEquals(10L, item.getRestaurantId());

        ItemEntity target = new ItemEntity();
        PersistenceMapper.copyToEntity(Fixtures.item(3L, 10L), target);
        assertEquals("/img/burger.png", target.getImageUrl());
        assertEquals("Tasty", target.getDescription());
    }

    @Test
    void mapsOrderWithCreationDate() {
        RestaurantEntity restaurant = EntityFixtures.restaurant(10L, EntityFixtures.user(1L, "owner"));
        OrderEntity entity = EntityFixtures.order(5L, restaurant, EntityFixtures.user(4L, "customer"),
                EntityFixtures.item(3L, restaurant));

        Order order = PersistenceMapper.toDomain(entity);
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        assertEquals(10L, order.getRestaurantId());
        assertEquals(4L, order.getUserId());
        assertEquals(LocalDateTime.of(2026, 1, 1, 12, 0), order.getCreatedAt());

        entity.setAudit(null);
        assertNull(PersistenceMapper.toDomain(entity).getCreatedAt());
    }

    @Test
    void mapsNullAddresses() {
        assertNull(PersistenceMapper.toDomain((br.com.fiap.restaurant.infrastructure.persistence.entity.AddressEmbeddable) null));
        assertNull(PersistenceMapper.toEmbeddable(null));
    }
}
