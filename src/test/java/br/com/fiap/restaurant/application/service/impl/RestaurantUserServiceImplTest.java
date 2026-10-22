package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.domain.repository.RestaurantUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUserServiceImplTest {
    @Mock RestaurantUserRepository repository;
    @InjectMocks RestaurantUserServiceImpl service;

    @Test void validatesAssociatedUser() {
        User user = User.builder().id(7L).login("jane").build();
        when(repository.existsById_RestaurantIdAndId_UserId(3L, 7L)).thenReturn(true);
        service.validateUserIsAssocieted(user, 3L);
        verify(repository).existsById_RestaurantIdAndId_UserId(3L, 7L);
    }

    @Test void rejectsUnassociatedUser() {
        User user = User.builder().id(7L).login("jane").build();
        when(repository.existsById_RestaurantIdAndId_UserId(3L, 7L)).thenReturn(false);
        assertThrows(ForbiddenOperationException.class, () -> service.validateUserIsAssocieted(user, 3L));
    }

    @Test void returnsRestaurantUser() {
        var expected = br.com.fiap.restaurant.domain.entity.RestaurantUser.builder().build();
        when(repository.findById_RestaurantIdAndId_UserId(3L, 7L)).thenReturn(Optional.of(expected));
        assertSame(expected, service.getRestaurantUser(7L, 3L));
    }

    @Test void rejectsMissingRestaurantUser() {
        when(repository.findById_RestaurantIdAndId_UserId(3L, 7L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getRestaurantUser(7L, 3L));
    }

    @Test void validatesRestaurantOwner() {
        User user = User.builder().id(7L).login("jane").build();
        when(repository.existsById_RestaurantIdAndId_UserIdAndUserType(3L, 7L, UserType.RESTAURANT_OWNER)).thenReturn(true);
        service.validateUserIsRestaurantOwner(3L, user);
    }

    @Test void rejectsNonOwner() {
        User user = User.builder().id(7L).login("jane").build();
        when(repository.existsById_RestaurantIdAndId_UserIdAndUserType(3L, 7L, UserType.RESTAURANT_OWNER)).thenReturn(false);
        assertThrows(ForbiddenOperationException.class, () -> service.validateUserIsRestaurantOwner(3L, user));
    }
}
