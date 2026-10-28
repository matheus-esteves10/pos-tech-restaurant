package br.com.fiap.restaurant.application.usecase.auth;

import br.com.fiap.restaurant.application.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurant.application.gateway.TokenGateway;
import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class LoginUseCase {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final TokenGateway tokenGateway;

    public LoginUseCase(UserRepository userRepository, PasswordEncoderGateway passwordEncoder,
                        TokenGateway tokenGateway) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGateway = tokenGateway;
    }

    public AuthToken execute(String login, String password) {
        User user = userRepository.findByLogin(login)
                .filter(User::isEnabled)
                .filter(found -> passwordEncoder.matches(password, found.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));

        return new AuthToken(tokenGateway.generateToken(user), TOKEN_TYPE, tokenGateway.getExpirationMs());
    }
}
