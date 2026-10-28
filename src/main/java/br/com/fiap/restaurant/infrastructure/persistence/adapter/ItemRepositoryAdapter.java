package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.ItemJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;

    @Override
    @Transactional
    public Item save(Item item) {
        ItemEntity entity;
        if (item.getId() == null) {
            entity = new ItemEntity();
            entity.setRestaurant(restaurantJpaRepository.getReferenceById(item.getRestaurantId()));
        } else {
            entity = itemJpaRepository.findById(item.getId()).orElseThrow(EntityNotFoundException::new);
        }

        PersistenceMapper.copyToEntity(item, entity);

        return PersistenceMapper.toDomain(itemJpaRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findByIdAndRestaurantId(Long itemId, Long restaurantId) {
        return itemJpaRepository.findByIdAndRestaurantId(itemId, restaurantId).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findAllByRestaurantId(Long restaurantId) {
        return itemJpaRepository.findAllByRestaurantId(restaurantId).stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}
