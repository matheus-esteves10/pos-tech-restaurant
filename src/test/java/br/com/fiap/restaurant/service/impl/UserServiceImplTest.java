package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.AddressRequest;
import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.exception.DuplicateResourceException;
import br.com.fiap.restaurant.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.exception.ResourceNotFoundException;
import br.com.fiap.restaurant.mapper.UserMapper;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import br.com.fiap.restaurant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .phone("11999999999")
                .password("encoded_password")
                .userType(UserType.CLIENT)
                .enabled(true)
                .build();

        createUserRequest = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, null
        );

        updateUserRequest = new UpdateUserRequest(
                "João Silva Atualizado", "joao.novo@email.com", "11988888888", null, null
        );
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userMapper.toEntity(any(CreateUserRequest.class), anyString())).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "João Silva", "joao@email.com", "joao123", "11999999999",
                        UserType.CLIENT, true, null)
        );

        UserResponse response = userService.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals("João Silva", response.name());
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).existsByLogin(createUserRequest.login());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateLogin() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByLogin(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).existsByLogin(createUserRequest.login());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicatePhone() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserSuccess() {
        User authenticatedUser = User.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "João Silva Atualizado", "joao.novo@email.com", "joao123",
                        "11988888888", UserType.CLIENT, true, null)
        );

        UserResponse response = userService.updateUser(1L, updateUserRequest, authenticatedUser);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).updateEntity(any(User.class), any(UpdateUserRequest.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        User authenticatedUser = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(1L, updateUserRequest, authenticatedUser));
    }

    @Test
    void testUpdateUserForbidden() {
        User authenticatedUser = User.builder().id(2L).build();

        assertThrows(ForbiddenOperationException.class, () ->
                userService.updateUser(1L, updateUserRequest, authenticatedUser));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testUpdateUserWithPasswordChange() {
        User authenticatedUser = User.builder().id(1L).build();
        UpdateUserRequest requestWithPassword = new UpdateUserRequest(
                "João Silva Atualizado", "joao.novo@email.com", "11988888888", null, "novaSenha123"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("encoded_nova_senha");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "João Silva Atualizado", "joao.novo@email.com", "joao123",
                        "11988888888", UserType.CLIENT, true, null)
        );

        userService.updateUser(1L, requestWithPassword, authenticatedUser);

        verify(passwordEncoder, times(1)).encode("novaSenha123");
    }

    @Test
    void testDeleteUserSuccess() {
        User authenticatedUser = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L, authenticatedUser);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        User authenticatedUser = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(1L, authenticatedUser));
    }

    @Test
    void testDeleteUserForbidden() {
        User authenticatedUser = User.builder().id(2L).build();

        assertThrows(ForbiddenOperationException.class, () ->
                userService.deleteUser(1L, authenticatedUser));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testCreateUserWithBlankPhone() {
        CreateUserRequest requestBlankPhone = new CreateUserRequest(
                "Maria Silva", "maria@email.com", "maria456", "",
                "senha456", UserType.RESTAURANT_OWNER, null
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userMapper.toEntity(any(CreateUserRequest.class), anyString())).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "Maria Silva", "maria@email.com", "maria456", "",
                        UserType.RESTAURANT_OWNER, true, null)
        );

        userService.createUser(requestBlankPhone);

        verify(userRepository, never()).existsByPhone(anyString());
    }
}
