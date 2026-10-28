package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.restaurant.CreateRestaurantCommand;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

public record CreateRestaurantRequest(@NotBlank(message = "Name cannot be blank") String name,
                                      @NotBlank(message = "CNPJ cannot be blank") String cnpj,
                                      String description,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone,
                                      @NotNull(message = "Kitchen type cannot be null") KitchenType kitchenType,
                                      @NotNull(message = "Opening time cannot be null") LocalTime openingAt,
                                      @NotNull(message = "Closing time cannot be null") LocalTime closingAt,
                                      @NotNull(message = "Address cannot be null") @Valid AddressRequest address) {

    public CreateRestaurantCommand toCommand() {
        return new CreateRestaurantCommand(name, cnpj, description, phone, kitchenType, openingAt, closingAt,
                AddressRequest.toAddress(address));
    }
}
