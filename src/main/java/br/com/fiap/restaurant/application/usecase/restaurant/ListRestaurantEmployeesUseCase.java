package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;

import java.util.List;

@UseCase
public class ListRestaurantEmployeesUseCase {

    private final RestaurantRepository restaurantRepository;

    public ListRestaurantEmployeesUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantMember> execute(Long restaurantId, User authenticatedUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(EntityNotFoundException::new);

        restaurant.ensureMember(authenticatedUser);

        return restaurant.getMembers();
    }
}
