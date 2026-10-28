package br.com.fiap.restaurant.infrastructure.persistence.jpa;

import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findByIdAndEnabledTrue(Long id);

    List<UserEntity> findAllByEnabledTrue();

    Page<UserEntity> findByNameIgnoreCaseAndEnabledTrue(String name, Pageable pageable);
}
