package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.Item;

import java.math.BigDecimal;

public record ItemResponse(Long id,
                           String name,
                           String description,
                           BigDecimal value) {

    public static ItemResponse fromItem(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getValue()
        );
    }
}
