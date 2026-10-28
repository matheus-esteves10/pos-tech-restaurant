package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantMemberEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurant.getId() == null
                ? new RestaurantEntity()
                : restaurantJpaRepository.findById(restaurant.getId()).orElseThrow(EntityNotFoundException::new);

        PersistenceMapper.copyToEntity(restaurant, entity);
        RestaurantEntity saved = restaurantJpaRepository.saveAndFlush(entity);

        syncMembers(restaurant.getMembers(), saved);
        restaurantJpaRepository.flush();

        return PersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> findActiveById(Long id) {
        return restaurantJpaRepository.findByIdAndEnabledTrue(id).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> findAllActive() {
        return restaurantJpaRepository.findAllByEnabledTrue().stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }

    // Members are mutated in place: replacing an orphanRemoval collection makes Hibernate fail.
    private void syncMembers(List<RestaurantMember> members, RestaurantEntity entity) {
        Map<Long, RestaurantMember> desired = members.stream()
                .collect(Collectors.toMap(member -> member.getUser().getId(), Function.identity()));

        entity.getMembers().removeIf(current -> !desired.containsKey(current.getUser().getId()));

        Set<Long> existingUserIds = new HashSet<>();
        for (RestaurantMemberEntity current : entity.getMembers()) {
            Long userId = current.getUser().getId();
            current.setUserType(desired.get(userId).getRole());
            existingUserIds.add(userId);
        }

        desired.values().stream()
                .filter(member -> !existingUserIds.contains(member.getUser().getId()))
                .map(member -> new RestaurantMemberEntity(
                        entity,
                        userJpaRepository.getReferenceById(member.getUser().getId()),
                        member.getRole()))
                .forEach(entity.getMembers()::add);
    }
}
