package br.com.fiap.restaurant.infrastructure.persistence.entity;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantEntity extends BaseEntity {

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
    private AddressEmbeddable address;

    @Column
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column
    private KitchenType kitchenType;

    @Column(nullable = false)
    private LocalTime openingAt;

    @Column(nullable = false)
    private LocalTime closingAt;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantMemberEntity> members = new ArrayList<>();
}
