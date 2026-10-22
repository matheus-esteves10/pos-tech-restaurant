package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.application.dto.response.RestaurantEmployeeResponse;
import br.com.fiap.restaurant.application.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.application.dto.response.RestaurantUserResponse;
import br.com.fiap.restaurant.application.service.impl.RestaurantServiceImpl;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {
    @Mock
    private RestaurantServiceImpl service;

    @InjectMocks
    private RestaurantController controller;

    @Test
    void delegatesRestaurantQueries() {
        var restaurant = response();
        var employee = new RestaurantEmployeeResponse("Jane", "jane", null, UserType.RESTAURANT_EMPLOYEE);
        User user = User.builder().id(1L).build();
        when(service.getAllRestaurants()).thenReturn(List.of(restaurant));
        when(service.getRestaurant(2L)).thenReturn(restaurant);
        when(service.getRestaurantEmployees(2L, user)).thenReturn(List.of(employee));
        assertEquals(List.of(restaurant), controller.getRestaurants().getBody());
        assertEquals(restaurant, controller.getRestaurant(2L).getBody());
        assertEquals(List.of(employee), controller.getRestaurantEmployees(2L, user).getBody());
    }

    @Test
    void createsAndUpdatesRestaurant() {
        User user = User.builder().id(1L).build();
        var create = new CreateRestaurantRequest("Cafe", "123", null, null, null, java.time.LocalTime.NOON, java.time.LocalTime.MIDNIGHT, null);
        var update = new UpdateRestaurantRequest("New cafe", null, null, null, null, null, null);
        var restaurant = response();
        when(service.createRestaurant(create, user)).thenReturn(restaurant);
        when(service.updateRestaurant(update, user, 2L)).thenReturn(restaurant);

        assertEquals(HttpStatus.CREATED, controller.createRestaurant(create, user).getStatusCode());
        assertEquals(restaurant, controller.updateRestaurant(update, user, 2L).getBody());
    }

    @Test
    void addsAndRemovesEmployee() {
        User user = User.builder().id(1L).build();
        var employee = new RestaurantUserResponse("Cafe", "staff", UserType.RESTAURANT_EMPLOYEE);
        when(service.addUser(2L, user, 3L)).thenReturn(employee);
        when(service.removeUser(2L, user, 3L)).thenReturn(employee);

        assertEquals(employee, controller.addEmployee(2L, user, 3L).getBody());
        assertEquals(employee, controller.removeEmployee(2L, user, 3L).getBody());
    }

    @Test
    void promotesEmployeeToOwner() {
        User user = User.builder().id(1L).build();
        assertEquals(HttpStatus.OK, controller.setUserAsOwner(2L, user, 3L).getStatusCode());
        verify(service).setAsOwner(2L, user, 3L);
    }

    private RestaurantResponse response() {
        return new RestaurantResponse(2L, "Cafe", "123", null, null, null, null, null, null, List.of());
    }
}
