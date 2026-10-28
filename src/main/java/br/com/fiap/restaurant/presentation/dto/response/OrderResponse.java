package br.com.fiap.restaurant.presentation.dto.response;

import br.com.fiap.restaurant.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id,
                            OrderStatus status,
                            List<ItemResponse> items,
                            Long restaurantId,
                            Long userId,
                            LocalDateTime timestamp) {
}
