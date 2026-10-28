package br.com.fiap.restaurant.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Embedded
    private AuditEmbeddable audit = new AuditEmbeddable();

    @Column(nullable = false)
    private Boolean enabled = true;

    // Hibernate leaves an embedded component null when all of its columns are null.
    @PrePersist
    @PreUpdate
    private void ensureAudit() {
        if (audit == null) {
            audit = new AuditEmbeddable();
        }
    }
}
