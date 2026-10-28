package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.item.ItemCommand;
import br.com.fiap.restaurant.domain.enums.ItemType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateItemRequest(String name,
                                String description,
                                @Positive(message = "Value must be greater than zero") BigDecimal value,
                                String imageUrl,
                                ItemType itemType) {

    public ItemCommand toCommand() {
        return new ItemCommand(name, description, value, imageUrl, itemType);
    }
}
