package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(User authenticatedUser, UpdateUserCommand command) {
        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(EntityNotFoundException::new);

        user.update(command.email(), command.login(), command.phone(), command.address());

        return userRepository.save(user);
    }
}
