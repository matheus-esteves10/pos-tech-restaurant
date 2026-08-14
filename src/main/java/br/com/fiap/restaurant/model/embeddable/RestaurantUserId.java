package br.com.fiap.restaurant.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantUserId implements Serializable {

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "user_id")
    private Long userId;
}

