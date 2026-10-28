package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoderGateway passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(CreateUserCommand command) {
        User user = User.create(
                command.name(),
                command.email(),
                command.login(),
                command.phone(),
                passwordEncoder.encode(command.password()),
                command.address()
        );
        return userRepository.save(user);
    }
}
