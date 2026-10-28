package br.com.fiap.restaurant.infrastructure.persistence.entity;

import br.com.fiap.restaurant.domain.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant_user")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantMemberEntity {

    @EmbeddedId
    private RestaurantMemberId id = new RestaurantMemberId();

    @MapsId("restaurantId")
    @ManyToOne
    @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(name = "fk_restaurant_user_restaurant"))
    private RestaurantEntity restaurant;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_restaurant_user_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    public RestaurantMemberEntity(RestaurantEntity restaurant, UserEntity user, UserType userType) {
        this.id = new RestaurantMemberId(restaurant.getId(), user.getId());
        this.restaurant = restaurant;
        this.user = user;
        this.userType = userType;
    }
}
