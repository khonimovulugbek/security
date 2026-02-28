package com.company.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Abstract base class supplying audit timestamps and soft-delete to all entities.
 *
 * <p>Hibernate maps {@code OffsetDateTime} → {@code TIMESTAMP WITH TIME ZONE} on Oracle,
 * which is the correct Oracle-native type for timezone-aware timestamps.</p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Soft-delete flag.  Never physically delete rows — set this to {@code true}
     * and exclude deleted rows from every query using {@code AND DELETED = 0}.
     */
    @Column(name = "DELETED", nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
