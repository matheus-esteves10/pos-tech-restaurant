package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.application.usecase.UseCase;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;

@UseCase
public class FindUsersByNameUseCase {

    private final UserRepository userRepository;

    public FindUsersByNameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PageResult<User> execute(String name, int page, int size) {
        PageResult<User> users = userRepository.findActiveByName(name, page, size);
        if (users.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return users;
    }
}
