package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.UserType;

import static br.com.fiap.restaurant.domain.model.Guard.requireNonNull;

public class RestaurantMember {

    private final User user;
    private UserType role;

    public RestaurantMember(User user, UserType role) {
        this.user = requireNonNull(user, "User");
        this.role = requireNonNull(role, "Role");
    }

    public void promoteToOwner() {
        this.role = UserType.RESTAURANT_OWNER;
    }

    public boolean isOwner() {
        return role == UserType.RESTAURANT_OWNER;
    }

    public boolean belongsTo(Long userId) {
        return user.getId() != null && user.getId().equals(userId);
    }

    public User getUser() {
        return user;
    }

    public UserType getRole() {
        return role;
    }
}
