package br.com.fiap.restaurant.model;

import br.com.fiap.restaurant.common.audit.Address;
import br.com.fiap.restaurant.model.embeddable.RestaurantUserId;
import br.com.fiap.restaurant.model.enums.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Table(name = "restaurant")
public class Restaurant extends DefaultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column
    private String description;

    @Embedded
    private Address address;

    @Column
    @Pattern(regexp = "\\d{11}", message = "Invalid phone")
    private String phone;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RestaurantUser> restaurantUsers = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    // User management methods
    public void addUser(User user, UserType userType) {
        RestaurantUserId restaurantUserId = RestaurantUserId.builder()
                .restaurantId(this.id)
                .userId(user.getId())
                .build();

        RestaurantUser restaurantUser = RestaurantUser.builder()
                .id(restaurantUserId)
                .restaurant(this)
                .user(user)
                .userType(userType)
                .build();

        this.restaurantUsers.add(restaurantUser);
    }

    public void removeUser(User user) {
        this.restaurantUsers.removeIf(ru -> ru.getUser().getId().equals(user.getId()));
    }

}
