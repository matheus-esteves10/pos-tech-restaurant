package br.com.fiap.restaurant.repository;

import br.com.fiap.restaurant.model.RestaurantUser;
import br.com.fiap.restaurant.model.embeddable.RestaurantUserId;
import br.com.fiap.restaurant.model.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantUserRepository extends JpaRepository<RestaurantUser, RestaurantUserId> {

    boolean existsById_RestaurantIdAndId_UserId(Long restaurantId, Long userId);

    boolean existsById_RestaurantIdAndId_UserIdAndUserType(Long restaurantId, Long userId,
            UserType userType);

    Optional<RestaurantUser> findById_RestaurantIdAndId_UserId(
            Long restaurantId,
            Long userId
    );
}