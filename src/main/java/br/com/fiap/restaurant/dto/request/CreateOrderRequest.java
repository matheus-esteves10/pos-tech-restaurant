package br.com.fiap.restaurant.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(@NotEmpty(message = "Items cannot be empty") List<Long> itemIds) {
}
