package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.BusinessRuleException;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.fiap.restaurant.domain.model.Guard.requireNonNull;
import static br.com.fiap.restaurant.domain.model.Guard.requireText;

public class Restaurant {

    private final Long id;
    private String name;
    private final String cnpj;
    private String description;
    private Address address;
    private String phone;
    private KitchenType kitchenType;
    private LocalTime openingAt;
    private LocalTime closingAt;
    private boolean enabled;
    private final List<RestaurantMember> members;

    public Restaurant(Long id, String name, String cnpj, String description, Address address, String phone,
                      KitchenType kitchenType, LocalTime openingAt, LocalTime closingAt, boolean enabled,
                      List<RestaurantMember> members) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.kitchenType = kitchenType;
        this.openingAt = openingAt;
        this.closingAt = closingAt;
        this.enabled = enabled;
        this.members = new ArrayList<>(members);
    }

    public static Restaurant create(String name, String cnpj, String description, Address address, String phone,
                                    KitchenType kitchenType, LocalTime openingAt, LocalTime closingAt, User owner) {
        requireText(name, "Name");
        requireText(cnpj, "CNPJ");
        requireNonNull(openingAt, "Opening time");
        requireNonNull(closingAt, "Closing time");
        requireNonNull(owner, "Owner");
        return new Restaurant(null, name, cnpj, description, address, phone, kitchenType, openingAt, closingAt,
                true, List.of(new RestaurantMember(owner, UserType.RESTAURANT_OWNER)));
    }

    public void update(String name, String description, Address address, String phone, KitchenType kitchenType,
                       LocalTime openingAt, LocalTime closingAt) {
        if (name != null) {
            this.name = requireText(name, "Name");
        }
        if (description != null) {
            this.description = description;
        }
        if (address != null) {
            this.address = address;
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (kitchenType != null) {
            this.kitchenType = kitchenType;
        }
        if (openingAt != null) {
            this.openingAt = openingAt;
        }
        if (closingAt != null) {
            this.closingAt = closingAt;
        }
    }

    public void addEmployee(User user) {
        requireNonNull(user, "User");
        if (hasMember(user.getId())) {
            throw new BusinessRuleException("User is already associated with this restaurant");
        }
        members.add(new RestaurantMember(user, UserType.RESTAURANT_EMPLOYEE));
    }

    public void removeMember(Long userId) {
        members.removeIf(member -> member.belongsTo(userId));
    }

    public void promoteToOwner(Long userId) {
        findMember(userId)
                .orElseThrow(EntityNotFoundException::new)
                .promoteToOwner();
    }

    public boolean hasMember(Long userId) {
        return findMember(userId).isPresent();
    }

    public boolean isOwner(Long userId) {
        return findMember(userId).map(RestaurantMember::isOwner).orElse(false);
    }

    public void ensureMember(User user) {
        if (!hasMember(user.getId())) {
            throw new ForbiddenOperationException(user.getLogin());
        }
    }

    public void ensureOwner(User user) {
        if (!isOwner(user.getId())) {
            throw new ForbiddenOperationException(user.getLogin());
        }
    }

    public Optional<RestaurantMember> findMember(Long userId) {
        return members.stream()
                .filter(member -> member.belongsTo(userId))
                .findFirst();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public KitchenType getKitchenType() {
        return kitchenType;
    }

    public LocalTime getOpeningAt() {
        return openingAt;
    }

    public LocalTime getClosingAt() {
        return closingAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<RestaurantMember> getMembers() {
        return List.copyOf(members);
    }
}
