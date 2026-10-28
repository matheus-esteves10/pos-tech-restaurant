package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.item.ItemCommand;
import br.com.fiap.restaurant.domain.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateItemRequest(@NotBlank(message = "Name cannot be blank") String name,
                                String description,
                                @NotNull(message = "Value cannot be null")
                                @Positive(message = "Value must be greater than zero") BigDecimal value,
                                String imageUrl,
                                ItemType itemType) {

    public ItemCommand toCommand() {
        return new ItemCommand(name, description, value, imageUrl, itemType);
    }
}
