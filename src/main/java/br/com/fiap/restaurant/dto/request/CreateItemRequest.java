package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Item;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Optional;

public record CreateItemRequest(@NotBlank(message = "Name cannot be blank") String name,
                                String description,
                                @NotNull(message = "Value cannot be null")
                                @Positive(message = "Value must be greater than zero") BigDecimal value,
                                String imageUrl,
                                ItemType itemType) {

    public static Item toItem(CreateItemRequest request, Restaurant restaurant) {
        Item.ItemBuilder<?, ?> builder = Item.builder()
                .name(request.name())
                .description(request.description())
                .value(request.value())
                .restaurant(restaurant)
                .imageUrl(request.imageUrl());

        Optional.ofNullable(request.itemType())
                .ifPresent(builder::itemType);

        return builder.build();
    }
}
