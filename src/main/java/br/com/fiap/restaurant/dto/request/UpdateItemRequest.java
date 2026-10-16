package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Item;
import br.com.fiap.restaurant.model.enums.ItemType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateItemRequest(String name,
                                String description,
                                @Positive(message = "Value must be greater than zero") BigDecimal value,
                                String imageUrl,
                                ItemType itemType) {

    public static void update(Item item, UpdateItemRequest request) {
        if (request.name() != null) {
            item.setName(request.name());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.value() != null) {
            item.setValue(request.value());
        }
        if (request.imageUrl() != null) {
            item.setImageUrl(request.imageUrl());
        }
        if (request.itemType() != null) {
            item.setItemType(request.itemType());
        }
    }
}
