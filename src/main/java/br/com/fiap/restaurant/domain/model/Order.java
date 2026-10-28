package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.OrderStatus;
import br.com.fiap.restaurant.domain.exception.InvalidOrderStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.fiap.restaurant.domain.model.Guard.requireNonNull;
import static br.com.fiap.restaurant.domain.model.Guard.requireNotEmpty;

public class Order {

    private final Long id;
    private OrderStatus status;
    private final List<Item> items;
    private final Long restaurantId;
    private final Long userId;
    private final LocalDateTime createdAt;

    public Order(Long id, OrderStatus status, List<Item> items, Long restaurantId, Long userId,
                 LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.items = List.copyOf(items);
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static Order create(Long restaurantId, Long userId, List<Item> items) {
        requireNonNull(restaurantId, "Restaurant");
        requireNonNull(userId, "User");
        requireNotEmpty(items, "Items");
        return new Order(null, OrderStatus.IN_PROGRESS, items, restaurantId, userId, null);
    }

    public void cancel() {
        ensureInProgress();
        this.status = OrderStatus.CANCELED;
    }

    public void deliver() {
        ensureInProgress();
        this.status = OrderStatus.DELIVERED;
    }

    private void ensureInProgress() {
        if (status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException("Order already delivered");
        }
        if (status == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException("Order already canceled");
        }
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<Item> getItems() {
        return items;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
