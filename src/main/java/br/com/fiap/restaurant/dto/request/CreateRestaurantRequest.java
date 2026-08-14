package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.Restaurant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateRestaurantRequest(@NotBlank String name,
                                      @NotBlank String cnpj,
                                      String description,
                                      @Pattern(regexp = "\\d{11}", message = "Invalid phone") String phone,
                                      @Valid AddressRequest addressRequest) {

    public static Restaurant toRestaurant(CreateRestaurantRequest request) {
        return Restaurant.builder()
                .name(request.name())
                .cnpj(request.cnpj())
                .description(request.description())
                .phone(request.phone())
                .address(AddressRequest.toAddress(request.addressRequest()))
                .build();
    }
}
