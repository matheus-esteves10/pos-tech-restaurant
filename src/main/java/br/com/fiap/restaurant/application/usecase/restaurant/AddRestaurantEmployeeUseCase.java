package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class AddRestaurantEmployeeUseCase {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public AddRestaurantEmployeeUseCase(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public RestaurantMembership execute(Long restaurantId, Long userId, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.ensureOwner(authenticatedUser);

        User employee = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.addEmployee(employee);

        Restaurant saved = restaurantRepository.save(restaurant);
        return new RestaurantMembership(saved, employee, UserType.RESTAURANT_EMPLOYEE);
    }
}
