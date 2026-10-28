package br.com.fiap.restaurant.support;

import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.model.Address;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.domain.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class Fixtures {

    private Fixtures() {
    }

    public static Address address() {
        return new Address("Main St", "10", "Center", "Sao Paulo", "SP", "01000-000", "Apt 1");
    }

    public static User user(Long id, String login) {
        return new User(id, "User " + login, login + "@mail.com", login, "11999999999", "hash", address(), true);
    }

    public static Restaurant restaurant(Long id, User owner, User... employees) {
        List<RestaurantMember> members = new ArrayList<>();
        members.add(new RestaurantMember(owner, UserType.RESTAURANT_OWNER));
        for (User employee : employees) {
            members.add(new RestaurantMember(employee, UserType.RESTAURANT_EMPLOYEE));
        }
        return new Restaurant(id, "Cafe", "12345678000199", "Nice place", address(), "11988887777",
                KitchenType.BRAZILIAN_FOOD, LocalTime.of(8, 0), LocalTime.of(22, 0), true, members);
    }

    public static Item item(Long id, Long restaurantId) {
        return new Item(id, "Burger", "Tasty", new BigDecimal("25.90"), "/img/burger.png",
                ItemType.DELIVERY_OR_IN_PERSON, restaurantId, true);
    }

    public static Order order(Long id, Long restaurantId, Long userId, OrderStatus status) {
        return new Order(id, status, List.of(item(1L, restaurantId)), restaurantId, userId,
                LocalDateTime.of(2026, 1, 1, 12, 0));
    }
}
