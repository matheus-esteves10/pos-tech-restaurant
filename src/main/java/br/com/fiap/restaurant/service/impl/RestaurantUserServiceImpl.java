package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.exception.EntityNotFoundException;
import br.com.fiap.restaurant.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.model.RestaurantUser;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.repository.RestaurantUserRepository;
import br.com.fiap.restaurant.service.RestaurantUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantUserServiceImpl implements RestaurantUserService {

    private final RestaurantUserRepository restaurantUserRepository;

    @Override
    public void validateUserIsAssocieted(User authenticatedUser, Long restaurantId) {
        boolean isAssocieted = restaurantUserRepository.existsById_RestaurantIdAndId_UserId(
                restaurantId,
                authenticatedUser.getId()
        );
        if (!isAssocieted) {
            throw new ForbiddenOperationException(authenticatedUser.getUsername());
        }
    }

    @Override
    public RestaurantUser getRestaurantUser(Long userId, Long restaurantId) {
        return restaurantUserRepository.findById_RestaurantIdAndId_UserId(restaurantId, userId)
                .orElseThrow(EntityNotFoundException::new);
    }


    @Override
    public void validateUserIsRestaurantOwner(Long restaurantId, User user) {
        boolean isOwner = restaurantUserRepository.existsById_RestaurantIdAndId_UserIdAndUserType(restaurantId,
                user.getId(),
                UserType.RESTAURANT_OWNER);

        if (!isOwner) {
            throw new ForbiddenOperationException(user.getUsername());
        }
    }
}
