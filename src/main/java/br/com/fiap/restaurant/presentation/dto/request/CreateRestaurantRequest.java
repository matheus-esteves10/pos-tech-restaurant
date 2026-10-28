package br.com.fiap.restaurant.presentation.dto.request;

import br.com.fiap.restaurant.application.usecase.restaurant.CreateRestaurantCommand;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

public record CreateRestaurantRequest(@NotBlank String name,
                                      @NotBlank String cnpj,
                                      String description,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone,
                                      KitchenType kitchenType,
                                      @NotNull LocalTime openingAt,
                                      @NotNull LocalTime closingAt,
                                      @Valid AddressRequest addressRequest) {

    public CreateRestaurantCommand toCommand() {
        return new CreateRestaurantCommand(name, cnpj, description, phone, kitchenType, openingAt, closingAt,
                AddressRequest.toAddress(addressRequest));
    }
}
