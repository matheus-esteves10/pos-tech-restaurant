package br.com.fiap.restaurant.application.dto.response;

import br.com.fiap.restaurant.domain.entity.Order;
import br.com.fiap.restaurant.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id,
                            OrderStatus status,
                            List<ItemResponse> items,
                            Long restaurantId,
                            Long userId,
                            LocalDateTime timestamp) {

    public static OrderResponse fromOrder(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(ItemResponse::fromItem)
                        .toList(),
                order.getRestaurant().getId(),
                order.getUser().getId(),
                order.getAudit().getCreatedAt()
        );
    }
}
