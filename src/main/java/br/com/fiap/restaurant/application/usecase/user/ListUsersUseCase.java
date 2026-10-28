package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

import java.util.List;

@UseCase
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.findAllActive();
    }
}
