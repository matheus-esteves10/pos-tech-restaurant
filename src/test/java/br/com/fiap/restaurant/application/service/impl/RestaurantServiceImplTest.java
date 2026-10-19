package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.application.service.RestaurantUserService;
import br.com.fiap.restaurant.application.service.UserService;
import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.entity.RestaurantUser;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {
    @Mock RestaurantRepository repository;
    @Mock RestaurantUserService restaurantUserService;
    @Mock UserService userService;
    @InjectMocks RestaurantServiceImpl service;

    @Test void createsRestaurantAndAddsOwner() {
        User owner = User.builder().id(4L).build();
        Restaurant saved = Restaurant.builder().id(2L).name("Cafe").cnpj("123").openingAt(LocalTime.NOON).closingAt(LocalTime.MIDNIGHT).build();
        when(repository.save(any(Restaurant.class))).thenReturn(saved);
        var response = service.createRestaurant(new CreateRestaurantRequest("Cafe", "123", null, null, null, LocalTime.NOON, LocalTime.MIDNIGHT, null), owner);
        assertEquals("Cafe", response.name());
        assertEquals(UserType.RESTAURANT_OWNER, saved.getRestaurantUsers().getFirst().getUserType());
        assertSame(owner, saved.getRestaurantUsers().getFirst().getUser());
    }

    @Test void updatesRestaurantAfterOwnerValidation() {
        Restaurant restaurant = restaurant(2L);
        User owner = User.builder().id(4L).build();
        when(repository.findById(2L)).thenReturn(Optional.of(restaurant));
        var response = service.updateRestaurant(new UpdateRestaurantRequest("New name", null, null, null, KitchenType.ITALIAN_FOOD, null, null), owner, 2L);
        assertEquals("New name", response.name());
        verify(restaurantUserService).validateUserIsRestaurantOwner(2L, owner);
    }

    @Test void addsAndRemovesRestaurantUser() {
        Restaurant restaurant = restaurant(2L);
        User employee = User.builder().id(5L).login("employee").build();
        when(repository.findById(2L)).thenReturn(Optional.of(restaurant));
        when(userService.findById(5L)).thenReturn(employee);
        var added = service.addUser(2L, User.builder().id(4L).build(), 5L);
        assertEquals(UserType.RESTAURANT_EMPLOYEE, added.userType());
        assertEquals(1, restaurant.getRestaurantUsers().size());
        var removed = service.removeUser(2L, User.builder().id(4L).build(), 5L);
        assertNull(removed.userType());
        assertTrue(restaurant.getRestaurantUsers().isEmpty());
    }

    @Test void promotesUserToOwner() {
        RestaurantUser restaurantUser = RestaurantUser.builder().userType(UserType.RESTAURANT_EMPLOYEE).build();
        when(restaurantUserService.getRestaurantUser(5L, 2L)).thenReturn(restaurantUser);
        service.setAsOwner(2L, User.builder().id(4L).build(), 5L);
        assertEquals(UserType.RESTAURANT_OWNER, restaurantUser.getUserType());
    }

    @Test void getsRestaurantsAndEmployees() {
        Restaurant restaurant = restaurant(2L);
        User employee = User.builder().name("Jane").login("jane").phone("12345678901").build();
        restaurant.addUser(employee, UserType.RESTAURANT_EMPLOYEE);
        when(repository.findById(2L)).thenReturn(Optional.of(restaurant));
        when(repository.findAll()).thenReturn(List.of(restaurant));
        assertEquals(1, service.getAllRestaurants().size());
        assertEquals("Cafe", service.getRestaurant(2L).name());
        assertEquals("jane", service.getRestaurantEmployees(2L, employee).getFirst().login());
    }

    @Test void rejectsUnknownRestaurant() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.findRestaurantById(2L));
        assertThrows(EntityNotFoundException.class, () -> service.getRestaurant(2L));
    }

    private Restaurant restaurant(Long id) {
        return Restaurant.builder().id(id).name("Cafe").cnpj("123").openingAt(LocalTime.NOON).closingAt(LocalTime.MIDNIGHT).build();
    }
}
