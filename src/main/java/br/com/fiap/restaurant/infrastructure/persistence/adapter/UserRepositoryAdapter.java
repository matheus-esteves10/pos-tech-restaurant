package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = user.getId() == null
                ? new UserEntity()
                : userJpaRepository.findById(user.getId()).orElseThrow(EntityNotFoundException::new);

        PersistenceMapper.copyToEntity(user, entity);

        return PersistenceMapper.toDomain(userJpaRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findActiveById(Long id) {
        return userJpaRepository.findByIdAndEnabledTrue(id).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        return userJpaRepository.findByLogin(login).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllActive() {
        return userJpaRepository.findAllByEnabledTrue().stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> findActiveByName(String name, int page, int size) {
        Page<UserEntity> result = userJpaRepository.findByNameIgnoreCaseAndEnabledTrue(
                name, PageRequest.of(page, size, Sort.by("name").ascending()));

        return new PageResult<>(
                result.getContent().stream().map(PersistenceMapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
