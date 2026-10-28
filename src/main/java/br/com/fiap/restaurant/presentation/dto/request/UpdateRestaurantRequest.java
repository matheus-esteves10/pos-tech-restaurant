package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.restaurant.UpdateRestaurantCommand;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

public record UpdateRestaurantRequest(String name,
                                      String description,
                                      @Valid AddressRequest address,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone,
                                      KitchenType kitchenType,
                                      LocalTime openingAt,
                                      LocalTime closingAt) {

    public UpdateRestaurantCommand toCommand() {
        return new UpdateRestaurantCommand(name, description, AddressRequest.toAddress(address), phone,
                kitchenType, openingAt, closingAt);
    }
}
