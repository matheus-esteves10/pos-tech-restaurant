package br.com.fiap.restaurant.application.dto.response;

import br.com.fiap.restaurant.domain.entity.Item;

import java.math.BigDecimal;

public record ItemResponse(Long id,
                           String name,
                           String description,
                           BigDecimal value,
                           String imageUrl,
                           String itemType) {

    public static ItemResponse fromItem(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getValue(),
                item.getImageUrl(),
                item.getItemType().name()
        );
    }
}
