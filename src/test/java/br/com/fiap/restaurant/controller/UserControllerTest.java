package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.dto.request.AddressRequest;
import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.exception.DuplicateResourceException;
import br.com.fiap.restaurant.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.exception.ResourceNotFoundException;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        authenticatedUser = User.builder()
                .id(1L)
                .login("joao123")
                .name("João Silva")
                .email("joao@email.com")
                .userType(UserType.CLIENT)
                .enabled(true)
                .build();
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        AddressRequest addressRequest = new AddressRequest(
                "Rua Principal", "123", "Centro", "São Paulo", "SP", "01234-567", "Apto 42"
        );
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, addressRequest
        );
        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com", "joao123",
                "11999999999", UserType.CLIENT, true, null);

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void testCreateUserWithDuplicateEmail() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, null
        );

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("Email already in use"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "João Silva", "invalid-email", "joao123", "11999999999",
                "senha123", UserType.CLIENT, null
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithShortName() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "Jo", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, null
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "João Silva Updated", "joao.updated@email.com", "11988888888", null, null
        );
        UserResponse userResponse = new UserResponse(1L, "João Silva Updated", "joao.updated@email.com",
                "joao123", "11988888888", UserType.CLIENT, true, null);

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class), any(User.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
                .principal(() -> authenticatedUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva Updated"));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "João Silva", "joao@email.com", null, null, null
        );

        when(userService.updateUser(eq(999L), any(UpdateUserRequest.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserForbidden() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "João Silva", "joao@email.com", null, null, null
        );

        when(userService.updateUser(eq(2L), any(UpdateUserRequest.class), any(User.class)))
                .thenThrow(new ForbiddenOperationException("You can only manage your own user account"));

        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserWithPassword() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "João Silva", "joao@email.com", null, null, "novaSenha123"
        );
        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com",
                "joao123", "11999999999", UserType.CLIENT, true, null);

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class), any(User.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(eq(1L), any(User.class));

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(eq(999L), any(User.class));

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserForbidden() throws Exception {
        doThrow(new ForbiddenOperationException("You can only manage your own user account"))
                .when(userService).deleteUser(eq(2L), any(User.class));

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isForbidden());
    }
}
