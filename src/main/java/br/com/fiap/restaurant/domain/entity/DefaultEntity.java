package br.com.fiap.restaurant.domain.entity;

import br.com.fiap.restaurant.domain.valueobject.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class DefaultEntity {

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @PrePersist
    @PreUpdate
    private void ensureAudit() {
        if (audit == null) {
            audit = new Audit();
        }
    }
}
