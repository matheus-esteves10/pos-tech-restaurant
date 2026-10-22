package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.application.dto.response.UserResponse;
import br.com.fiap.restaurant.application.service.impl.UserServiceImpl;
import br.com.fiap.restaurant.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock UserServiceImpl service;
    @InjectMocks UserController controller;

    @Test void delegatesUserQueries() {
        UserResponse user = response();
        when(service.getAllUsers()).thenReturn(List.of(user));
        when(service.getUser(2L)).thenReturn(user);
        when(service.findByName(eq("Jane"), any())).thenReturn(new PageImpl<>(List.of(user)));
        assertEquals(List.of(user), controller.getUsers().getBody());
        assertEquals(user, controller.getUser(2L).getBody());
        assertEquals(1, controller.findUserByName("Jane", PageRequest.of(0, 10)).getBody().getTotalElements());
    }

    @Test void delegatesUserMutationsAndReturnsExpectedStatuses() {
        User authenticated = User.builder().id(1L).build();
        UserResponse user = response();
        var create = new CreateUserRequest("Jane", "jane@example.com", "jane", null, "password123", null);
        var update = new UpdateUserRequest(null, "jane2", null, null);
        var password = new UpdateUserPasswordRequest("newpassword");
        when(service.createUser(create)).thenReturn(user);
        when(service.updateUser(update, authenticated)).thenReturn(user);
        when(service.updatePassword(password, authenticated)).thenReturn(user);
        assertEquals(HttpStatus.CREATED, controller.createUser(create).getStatusCode());
        assertEquals(user, controller.updateUser(update, authenticated).getBody());
        assertEquals(user, controller.updatePassword(password, authenticated).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteUser(authenticated).getStatusCode());
        verify(service).deleteUser(authenticated);
    }

    private UserResponse response() {
        return new UserResponse(1L, "Jane", "jane@example.com", "jane", null, true, null);
    }
}
