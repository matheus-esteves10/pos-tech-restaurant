package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.enums.KitchenType;
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

    public static Restaurant toRestaurant(CreateRestaurantRequest request) {
        Restaurant.RestaurantBuilder<?, ?> builder = Restaurant.builder()
                .name(request.name())
                .cnpj(request.cnpj())
                .openingAt(request.openingAt())
                .closingAt(request.closingAt());

        if (request.description() != null) {
            builder.description(request.description());
        }

        if (request.phone() != null) {
            builder.phone(request.phone());
        }

        if (request.kitchenType() != null) {
            builder.kitchenType(request.kitchenType());
        }

        if (request.addressRequest() != null) {
            builder.address(AddressRequest.toAddress(request.addressRequest()));
        }

        return builder.build();
    }
}
