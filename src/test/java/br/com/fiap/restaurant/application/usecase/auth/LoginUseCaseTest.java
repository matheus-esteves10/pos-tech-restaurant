package br.com.fiap.restaurant.application.usecase.auth;

import br.com.fiap.restaurant.application.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurant.application.gateway.TokenGateway;
import br.com.fiap.restaurant.domain.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoderGateway passwordEncoder;
    @Mock TokenGateway tokenGateway;
    @InjectMocks LoginUseCase useCase;

    @Test
    void returnsBearerTokenForValidCredentials() {
        User user = Fixtures.user(1L, "jane");
        when(userRepository.findByLogin("jane")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(tokenGateway.generateToken(user)).thenReturn("jwt");
        when(tokenGateway.getExpirationMs()).thenReturn(3600L);

        assertEquals(new AuthToken("jwt", "Bearer", 3600L), useCase.execute("jane", "secret"));
    }

    @Test
    void rejectsUnknownUserWrongPasswordAndDisabledUser() {
        when(userRepository.findByLogin("missing")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> useCase.execute("missing", "secret"));

        when(userRepository.findByLogin("jane")).thenReturn(Optional.of(Fixtures.user(1L, "jane")));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> useCase.execute("jane", "wrong"));

        User disabled = Fixtures.user(2L, "gone");
        disabled.disable();
        when(userRepository.findByLogin("gone")).thenReturn(Optional.of(disabled));
        assertThrows(InvalidCredentialsException.class, () -> useCase.execute("gone", "secret"));
    }
}
