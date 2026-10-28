package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User authenticatedUser) {
        User user = userRepository.findActiveById(authenticatedUser.getId())
                .orElseThrow(EntityNotFoundException::new);

        user.disable();

        userRepository.save(user);
    }
}
