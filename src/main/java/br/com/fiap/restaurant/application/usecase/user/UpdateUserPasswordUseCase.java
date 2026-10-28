package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;

    public UpdateUserPasswordUseCase(UserRepository userRepository, PasswordEncoderGateway passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(User authenticatedUser, String newPassword) {
        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(EntityNotFoundException::new);

        user.changePassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }
}
