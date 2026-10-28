package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.domain.enums.ItemType;

import java.math.BigDecimal;

public record ItemCommand(String name,
                          String description,
                          BigDecimal value,
                          String imageUrl,
                          ItemType itemType) {
}
