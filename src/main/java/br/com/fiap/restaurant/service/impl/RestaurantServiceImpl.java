package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.repository.RestaurantRepository;
import br.com.fiap.restaurant.service.RestaurantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantRequest restaurantRequest, User authenticatedUser) {
        Restaurant restaurant = CreateRestaurantRequest.toRestaurant(restaurantRequest);

        restaurant = restaurantRepository.save(restaurant);

        restaurant.addUser(
                authenticatedUser,
                UserType.RESTAURANT_OWNER
        );

        return RestaurantResponse.fromRestaurant(restaurant);
    }
}
