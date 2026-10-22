package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock UserRepository repository;
    @InjectMocks UserDetailsServiceImpl service;

    @Test void loadsUserByLogin() {
        User user = User.builder().login("jane").build();
        when(repository.findByLogin("jane")).thenReturn(Optional.of(user));
        assertSame(user, service.loadUserByUsername("jane"));
    }

    @Test void reportsUnknownLogin() {
        when(repository.findByLogin("missing")).thenReturn(Optional.empty());
        assertEquals("User not found with login: missing", assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")).getMessage());
    }
}
