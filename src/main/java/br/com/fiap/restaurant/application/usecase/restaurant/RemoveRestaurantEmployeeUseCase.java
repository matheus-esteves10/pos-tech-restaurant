package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class RemoveRestaurantEmployeeUseCase {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RemoveRestaurantEmployeeUseCase(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public RestaurantMembership execute(Long restaurantId, Long userId, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findActiveById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.ensureOwner(authenticatedUser);

        User user = userRepository.findActiveById(userId)
                .orElseThrow(EntityNotFoundException::new);
        restaurant.removeMember(user.getId());

        Restaurant saved = restaurantRepository.save(restaurant);
        return new RestaurantMembership(saved, user, null);
    }
}
