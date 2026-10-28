package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.usecase.user.CreateUserCommand;
import br.com.fiap.restaurant.application.usecase.user.CreateUserUseCase;
import br.com.fiap.restaurant.application.usecase.user.DeleteUserUseCase;
import br.com.fiap.restaurant.application.usecase.user.FindUsersByNameUseCase;
import br.com.fiap.restaurant.application.usecase.user.GetUserUseCase;
import br.com.fiap.restaurant.application.usecase.user.ListUsersUseCase;
import br.com.fiap.restaurant.application.usecase.user.UpdateUserCommand;
import br.com.fiap.restaurant.application.usecase.user.UpdateUserPasswordUseCase;
import br.com.fiap.restaurant.application.usecase.user.UpdateUserUseCase;
import br.com.fiap.restaurant.domain.model.Address;
import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.presentation.dto.request.AddressRequest;
import br.com.fiap.restaurant.presentation.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.presentation.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.presentation.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.presentation.dto.response.UserResponse;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock ListUsersUseCase listUsersUseCase;
    @Mock GetUserUseCase getUserUseCase;
    @Mock CreateUserUseCase createUserUseCase;
    @Mock UpdateUserUseCase updateUserUseCase;
    @Mock DeleteUserUseCase deleteUserUseCase;
    @Mock UpdateUserPasswordUseCase updateUserPasswordUseCase;
    @Mock FindUsersByNameUseCase findUsersByNameUseCase;
    @InjectMocks UserController controller;

    private final User user = Fixtures.user(1L, "jane");

    @Test
    void listsAndGetsUsers() {
        when(listUsersUseCase.execute()).thenReturn(List.of(user));
        when(getUserUseCase.execute(1L)).thenReturn(user);

        List<UserResponse> users = controller.getUsers().getBody();
        UserResponse single = controller.getUser(1L).getBody();

        assertEquals(1, users.size());
        assertEquals("jane", single.login());
        assertEquals("Main St", single.address().street());
        assertTrue(single.enabled());
    }

    @Test
    void createsUserFromRequest() {
        AddressRequest address = new AddressRequest("Main St", "10", "Center", "Sao Paulo", "SP", "01000-000", "Apt 1");
        CreateUserRequest request = new CreateUserRequest("Jane Doe", "jane@mail.com", "jane", null, "secret123", address);
        when(createUserUseCase.execute(new CreateUserCommand("Jane Doe", "jane@mail.com", "jane", null, "secret123",
                Fixtures.address()))).thenReturn(user);

        var response = controller.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void updatesDeletesAndChangesPassword() {
        when(updateUserUseCase.execute(user, new UpdateUserCommand("new@mail.com", null, null, null))).thenReturn(user);
        when(updateUserPasswordUseCase.execute(user, "new-secret")).thenReturn(user);

        assertEquals(HttpStatus.OK, controller.updateUser(new UpdateUserRequest("new@mail.com", null, null, null), user).getStatusCode());
        assertEquals(HttpStatus.OK, controller.updatePassword(new UpdateUserPasswordRequest("new-secret"), user).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteUser(user).getStatusCode());
        verify(deleteUserUseCase).execute(user);
    }

    @Test
    void findsUsersByNamePaged() {
        when(findUsersByNameUseCase.execute("jane", 1, 5)).thenReturn(new PageResult<>(List.of(user), 1, 5, 6, 2));

        var body = controller.findUserByName("jane", PageRequest.of(1, 5)).getBody();

        assertEquals(1, body.content().size());
        assertEquals(1, body.page());
        assertEquals(5, body.size());
        assertEquals(6, body.totalElements());
        assertEquals(2, body.totalPages());
    }

    @Test
    void mapsNullAddress() {
        assertNull(AddressRequest.toAddress(null));
        User withoutAddress = new User(2L, "John", "john@mail.com", "john", null, "hash", null, true);
        when(getUserUseCase.execute(2L)).thenReturn(withoutAddress);
        assertNull(controller.getUser(2L).getBody().address());
        assertEquals(new Address("a", null, null, null, null, null, null),
                AddressRequest.toAddress(new AddressRequest("a", null, null, null, null, null, null)));
    }
}
