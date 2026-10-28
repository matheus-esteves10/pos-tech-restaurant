package br.com.fiap.restaurant.infrastructure.persistence;

import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.infrastructure.persistence.entity.AddressEmbeddable;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantMemberEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class EntityFixtures {

    private EntityFixtures() {
    }

    public static UserEntity user(Long id, String login) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setName("User " + login);
        entity.setEmail(login + "@mail.com");
        entity.setLogin(login);
        entity.setPhone("11999999999");
        entity.setPassword("hash");
        entity.setAddress(new AddressEmbeddable("Main St", "10", "Center", "Sao Paulo", "SP", "01000-000", null));
        return entity;
    }

    public static RestaurantEntity restaurant(Long id, UserEntity owner) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(id);
        entity.setName("Cafe");
        entity.setCnpj("123");
        entity.setKitchenType(KitchenType.FAST_FOOD);
        entity.setOpeningAt(LocalTime.NOON);
        entity.setClosingAt(LocalTime.MIDNIGHT);
        entity.setMembers(new ArrayList<>(List.of(new RestaurantMemberEntity(entity, owner, UserType.RESTAURANT_OWNER))));
        return entity;
    }

    public static ItemEntity item(Long id, RestaurantEntity restaurant) {
        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName("Burger");
        entity.setValue(BigDecimal.TEN);
        entity.setItemType(ItemType.DELIVERY_OR_IN_PERSON);
        entity.setRestaurant(restaurant);
        return entity;
    }

    public static OrderEntity order(Long id, RestaurantEntity restaurant, UserEntity user, ItemEntity item) {
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setStatus(OrderStatus.IN_PROGRESS);
        entity.setRestaurant(restaurant);
        entity.setUser(user);
        entity.setItems(new ArrayList<>(List.of(item)));
        entity.getAudit().setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));
        return entity;
    }
}
