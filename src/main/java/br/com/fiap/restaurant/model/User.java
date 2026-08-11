package br.com.fiap.restaurant.model;

import br.com.fiap.restaurant.common.audit.Address;
import br.com.fiap.restaurant.common.audit.Audit;
import br.com.fiap.restaurant.model.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "User type cannot be null")
    private UserType userType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Order> orders; //TODO adicionar algo do tipo para pedidos do user

    @Embedded
    private Address address;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

    @PrePersist
    @PreUpdate
    private void ensureAudit() {
        if (audit == null) {
            audit = new Audit();
        }
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType.toString()));
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
        return enabled != null && enabled;
    }
}
