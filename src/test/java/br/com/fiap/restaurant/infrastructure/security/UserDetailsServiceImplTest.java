package br.com.fiap.restaurant.infrastructure.security;

import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.support.Fixtures;
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

    @Test void loadsUserByLoginWrappedAsPrincipal() {
        User user = Fixtures.user(1L, "jane");
        when(repository.findByLogin("jane")).thenReturn(Optional.of(user));
        AuthenticatedUser principal = (AuthenticatedUser) service.loadUserByUsername("jane");
        assertSame(user, principal.getUser());
    }

    @Test void reportsUnknownLogin() {
        when(repository.findByLogin("missing")).thenReturn(Optional.empty());
        assertEquals("User not found with login: missing", assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")).getMessage());
    }
}
