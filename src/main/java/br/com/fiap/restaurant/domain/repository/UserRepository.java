package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByLogin(String login);

    List<User> findAll();

    PageResult<User> findByName(String name, int page, int size);
}
