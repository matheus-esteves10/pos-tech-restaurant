package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.application.mapper.UserMapper;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserMapper mapper;
    @InjectMocks UserServiceImpl service;

    @Test void createsUserWithEncodedPassword() {
        var request = new CreateUserRequest("Jane Doe", "jane@example.com", "jane", null, "password123", null);
        User user = User.builder().id(1L).build();
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(mapper.toEntity(request, "encoded")).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(new br.com.fiap.restaurant.application.dto.response.UserResponse(1L, "Jane Doe", "jane@example.com", "jane", null, true, null));
        assertEquals(1L, service.createUser(request).id());
    }

    @Test void updatesAndSoftDeletesAuthenticatedUser() {
        User user = User.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(new br.com.fiap.restaurant.application.dto.response.UserResponse(1L, null, null, null, null, false, null));
        service.updateUser(new UpdateUserRequest(null, "newlogin", null, null), user);
        verify(mapper).updateEntity(user, new UpdateUserRequest(null, "newlogin", null, null));
        service.deleteUser(user);
        assertFalse(user.getEnabled());
        verify(repository, times(2)).save(user);
    }

    @Test void updatesPasswordUsingEncoder() {
        User user = User.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("hash");
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(new br.com.fiap.restaurant.application.dto.response.UserResponse(1L, null, null, null, null, true, null));
        service.updatePassword(new UpdateUserPasswordRequest("newpassword"), user);
        assertEquals("hash", user.getPassword());
    }

    @Test void findsUsersAndMapsPageAndList() {
        User user = User.builder().id(1L).build();
        var pageable = PageRequest.of(0, 10);
        when(repository.findByNameIgnoreCase("Jane", pageable)).thenReturn(new PageImpl<>(List.of(user)));
        when(repository.findAll()).thenReturn(List.of(user));
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(new br.com.fiap.restaurant.application.dto.response.UserResponse(1L, null, null, null, null, true, null));
        assertEquals(1, service.findByName("Jane", pageable).getTotalElements());
        assertEquals(1, service.getAllUsers().size());
        assertEquals(1L, service.getUser(1L).id());
        assertSame(user, service.findById(1L));
    }

    @Test void throwsWhenUserDoesNotExistOrSearchIsEmpty() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        when(repository.findByNameIgnoreCase(eq("Nobody"), any())).thenReturn(new PageImpl<>(List.of()));
        assertThrows(EntityNotFoundException.class, () -> service.findById(2L));
        assertThrows(EntityNotFoundException.class, () -> service.getUser(2L));
        assertThrows(EntityNotFoundException.class, () -> service.findByName("Nobody", PageRequest.of(0, 10)));
    }
}
