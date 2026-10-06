package br.com.fiap.restaurant.repository;

import br.com.fiap.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Page<User> findByNameIgnoreCase(String name, Pageable pageable);
}
