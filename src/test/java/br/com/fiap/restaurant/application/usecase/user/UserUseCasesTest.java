package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoderGateway passwordEncoder;

    private final User authenticated = Fixtures.user(1L, "jane");

    @Test
    void createsUserWithEncodedPassword() {
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = new CreateUserUseCase(userRepository, passwordEncoder).execute(
                new CreateUserCommand("Jane Doe", "jane@mail.com", "jane", null, "secret123", Fixtures.address()));

        assertEquals("encoded", created.getPassword());
        assertEquals("Jane Doe", created.getName());
        assertTrue(created.isEnabled());
    }

    @Test
    void updatesAuthenticatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(Fixtures.user(1L, "jane")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = new UpdateUserUseCase(userRepository).execute(authenticated,
                new UpdateUserCommand("new@mail.com", null, null, null));

        assertEquals("new@mail.com", updated.getEmail());
    }

    @Test
    void updatesPasswordAndSoftDeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(Fixtures.user(1L, "jane")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");

        User updated = new UpdateUserPasswordUseCase(userRepository, passwordEncoder).execute(authenticated, "new-secret");
        assertEquals("new-hash", updated.getPassword());

        new DeleteUserUseCase(userRepository).execute(authenticated);
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository, org.mockito.Mockito.times(2)).save(saved.capture());
        assertFalse(saved.getValue().isEnabled());
    }

    @Test
    void failsWhenAuthenticatedUserNoLongerExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> new UpdateUserUseCase(userRepository)
                .execute(authenticated, new UpdateUserCommand(null, null, null, null)));
        assertThrows(EntityNotFoundException.class, () -> new UpdateUserPasswordUseCase(userRepository, passwordEncoder)
                .execute(authenticated, "x"));
        assertThrows(EntityNotFoundException.class, () -> new DeleteUserUseCase(userRepository).execute(authenticated));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getsAndListsUsers() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(authenticated));
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(List.of(authenticated));

        assertSame(authenticated, new GetUserUseCase(userRepository).execute(1L));
        assertThrows(EntityNotFoundException.class, () -> new GetUserUseCase(userRepository).execute(9L));
        assertEquals(List.of(authenticated), new ListUsersUseCase(userRepository).execute());
    }

    @Test
    void findsUsersByNameOrFailsWhenEmpty() {
        PageResult<User> page = new PageResult<>(List.of(authenticated), 0, 10, 1, 1);
        when(userRepository.findByName("jane", 0, 10)).thenReturn(page);
        when(userRepository.findByName("nobody", 0, 10)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0));

        FindUsersByNameUseCase useCase = new FindUsersByNameUseCase(userRepository);

        assertSame(page, useCase.execute("jane", 0, 10));
        assertThrows(EntityNotFoundException.class, () -> useCase.execute("nobody", 0, 10));
    }
}
