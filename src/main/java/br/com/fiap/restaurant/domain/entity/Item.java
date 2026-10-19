package br.com.fiap.restaurant.domain.entity;

import br.com.fiap.restaurant.domain.enums.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import static br.com.fiap.restaurant.domain.enums.ItemType.DELIVERY_OR_IN_PERSON;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Table(name = "item")
public class Item extends DefaultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @NotNull(message = "Value cannot be null")
    @Column(nullable = false)
    private BigDecimal value;

    @Column(unique = true)
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Item type cannot be null")
    @Builder.Default
    private ItemType itemType = DELIVERY_OR_IN_PERSON;

    @NotNull(message = "Restaurant cannot be null")
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_restaurant"))
    private Restaurant restaurant;
}
