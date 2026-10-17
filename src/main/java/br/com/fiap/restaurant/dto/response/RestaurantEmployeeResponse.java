package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.RestaurantUser;
import br.com.fiap.restaurant.model.enums.UserType;

public record RestaurantEmployeeResponse(String name,
                                         String login,
                                         String phone,
                                         UserType role) {

    public static RestaurantEmployeeResponse fromRestaurantUser(RestaurantUser restaurantUser) {
        return new RestaurantEmployeeResponse(
                restaurantUser.getUser().getName(),
                restaurantUser.getUser().getLogin(),
                restaurantUser.getUser().getPhone(),
                restaurantUser.getUserType()
        );
    }
}
