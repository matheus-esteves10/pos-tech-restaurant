package br.com.fiap.restaurant.model;

import br.com.fiap.restaurant.common.audit.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Table(name = "users")
public class User extends DefaultEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Email
    @Column(unique = true, nullable = false)
    @NotBlank
    private String email;

    @NotBlank(message = "Login cannot be blank")
    @Column(unique = true, nullable = false)
    private String login;

    @Pattern(regexp = "\\d{11}", message = "Invalid phone")
    @Column(unique = true)
    private String phone;

    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantUser> restaurants;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Order> orders; //TODO adicionar algo do tipo para pedidos do user

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (restaurants == null || restaurants.isEmpty()) {
            return List.of();
        }
        return restaurants.stream()
                .map(ru -> new SimpleGrantedAuthority("ROLE_" + ru.getUserType().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isEnabled() {
        return getEnabled() != null && getEnabled();
    }
}
