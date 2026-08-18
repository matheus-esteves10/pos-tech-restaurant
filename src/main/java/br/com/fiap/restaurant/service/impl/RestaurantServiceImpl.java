package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.dto.response.RestaurantUserResponse;
import br.com.fiap.restaurant.exception.EntityNotFoundException;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.RestaurantUser;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.repository.RestaurantRepository;
import br.com.fiap.restaurant.service.RestaurantService;
import br.com.fiap.restaurant.service.RestaurantUserService;
import br.com.fiap.restaurant.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantUserService restaurantUserService;
    private final UserService userService;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest restaurantRequest, User authenticatedUser) {
        Restaurant restaurant = CreateRestaurantRequest.toRestaurant(restaurantRequest);

        restaurant = restaurantRepository.save(restaurant);

        restaurant.addUser(
                authenticatedUser,
                UserType.RESTAURANT_OWNER
        );

        return RestaurantResponse.fromRestaurant(restaurant);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(UpdateRestaurantRequest restaurantRequest, User authenticatedUser, Long restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        restaurantUserService.validateUserIsRestaurantOwner(restaurantId, authenticatedUser);

        UpdateRestaurantRequest.update(restaurant, restaurantRequest);

        return RestaurantResponse.fromRestaurant(restaurant);
    }

    @Transactional
    public RestaurantUserResponse addUser(Long restaurantId, User authenticatedUser, Long userId) {
        restaurantUserService.validateUserIsRestaurantOwner(restaurantId, authenticatedUser);

        Restaurant restaurant = findRestaurantById(restaurantId);
        User user = userService.findById(userId);

        restaurant.addUser(user, UserType.RESTAURANT_EMPLOYEE);

        return new RestaurantUserResponse(restaurant.getName(), user.getLogin(), UserType.RESTAURANT_EMPLOYEE);
    }

    @Transactional
    public RestaurantUserResponse removeUser(Long restaurantId, User authenticatedUser, Long userId) {
        restaurantUserService.validateUserIsRestaurantOwner(restaurantId, authenticatedUser);

        Restaurant restaurant = findRestaurantById(restaurantId);
        User user = userService.findById(userId);

        restaurant.removeUser(user);
        return new RestaurantUserResponse(restaurant.getName(), user.getLogin(), null);
    }

    @Transactional
    public void setAsOwner(Long restaurantId, User authenticatedUser, Long userId) {
        restaurantUserService.validateUserIsRestaurantOwner(restaurantId, authenticatedUser);

        RestaurantUser restaurantUser = restaurantUserService.getRestaurantUser(userId, restaurantId);

        restaurantUser.setUserType(UserType.RESTAURANT_OWNER);
    }


    public Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
