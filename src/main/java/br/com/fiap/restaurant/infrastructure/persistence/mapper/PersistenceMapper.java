package br.com.fiap.restaurant.infrastructure.persistence.mapper;

import br.com.fiap.restaurant.domain.model.Address;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.infrastructure.persistence.entity.AddressEmbeddable;
import br.com.fiap.restaurant.infrastructure.persistence.entity.BaseEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.ItemEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.RestaurantMemberEntity;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;

public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getLogin(),
                entity.getPhone(),
                entity.getPassword(),
                toDomain(entity.getAddress()),
                isEnabled(entity)
        );
    }

    public static void copyToEntity(User user, UserEntity entity) {
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setLogin(user.getLogin());
        entity.setPhone(user.getPhone());
        entity.setPassword(user.getPassword());
        entity.setAddress(toEmbeddable(user.getAddress()));
        entity.setEnabled(user.isEnabled());
    }

    public static Restaurant toDomain(RestaurantEntity entity) {
        return new Restaurant(
                entity.getId(),
                entity.getName(),
                entity.getCnpj(),
                entity.getDescription(),
                toDomain(entity.getAddress()),
                entity.getPhone(),
                entity.getKitchenType(),
                entity.getOpeningAt(),
                entity.getClosingAt(),
                isEnabled(entity),
                entity.getMembers().stream().map(PersistenceMapper::toDomain).toList()
        );
    }

    public static void copyToEntity(Restaurant restaurant, RestaurantEntity entity) {
        entity.setName(restaurant.getName());
        entity.setCnpj(restaurant.getCnpj());
        entity.setDescription(restaurant.getDescription());
        entity.setAddress(toEmbeddable(restaurant.getAddress()));
        entity.setPhone(restaurant.getPhone());
        entity.setKitchenType(restaurant.getKitchenType());
        entity.setOpeningAt(restaurant.getOpeningAt());
        entity.setClosingAt(restaurant.getClosingAt());
        entity.setEnabled(restaurant.isEnabled());
    }

    public static RestaurantMember toDomain(RestaurantMemberEntity entity) {
        return new RestaurantMember(toDomain(entity.getUser()), entity.getUserType());
    }

    public static Item toDomain(ItemEntity entity) {
        return new Item(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getValue(),
                entity.getImageUrl(),
                entity.getItemType(),
                entity.getRestaurant().getId(),
                isEnabled(entity)
        );
    }

    public static void copyToEntity(Item item, ItemEntity entity) {
        entity.setName(item.getName());
        entity.setDescription(item.getDescription());
        entity.setValue(item.getValue());
        entity.setImageUrl(item.getImageUrl());
        entity.setItemType(item.getItemType());
        entity.setEnabled(item.isEnabled());
    }

    public static Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getStatus(),
                entity.getItems().stream().map(PersistenceMapper::toDomain).toList(),
                entity.getRestaurant().getId(),
                entity.getUser().getId(),
                entity.getAudit() != null ? entity.getAudit().getCreatedAt() : null
        );
    }

    public static Address toDomain(AddressEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new Address(
                embeddable.getStreet(),
                embeddable.getNumber(),
                embeddable.getNeighborhood(),
                embeddable.getCity(),
                embeddable.getState(),
                embeddable.getZipCode(),
                embeddable.getComplement()
        );
    }

    public static AddressEmbeddable toEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressEmbeddable(
                address.street(),
                address.number(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.zipCode(),
                address.complement()
        );
    }

    private static boolean isEnabled(BaseEntity entity) {
        return Boolean.TRUE.equals(entity.getEnabled());
    }
}
