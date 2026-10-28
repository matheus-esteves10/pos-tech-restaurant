package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.repository.OrderRepository;
import br.com.fiap.restaurant.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.ItemJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.OrderJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.RestaurantJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity;
        if (order.getId() == null) {
            entity = new OrderEntity();
            entity.setRestaurant(restaurantJpaRepository.getReferenceById(order.getRestaurantId()));
            entity.setUser(userJpaRepository.getReferenceById(order.getUserId()));
            entity.setItems(new ArrayList<>(order.getItems().stream()
                    .map(Item::getId)
                    .map(itemJpaRepository::getReferenceById)
                    .toList()));
        } else {
            entity = orderJpaRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        }

        entity.setStatus(order.getStatus());

        return PersistenceMapper.toDomain(orderJpaRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByIdAndRestaurantId(Long orderId, Long restaurantId) {
        return orderJpaRepository.findByIdAndRestaurantId(orderId, restaurantId).map(PersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllByRestaurantId(Long restaurantId) {
        return orderJpaRepository.findAllByRestaurantId(restaurantId).stream()
                .map(PersistenceMapper::toDomain)
                .toList();
    }
}
