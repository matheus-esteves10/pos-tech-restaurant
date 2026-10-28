package br.com.fiap.restaurant.presentation.dto.response;

import java.math.BigDecimal;

public record ItemResponse(Long id,
                           String name,
                           String description,
                           BigDecimal value,
                           String imageUrl,
                           String itemType) {
}
