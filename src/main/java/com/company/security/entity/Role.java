package com.company.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * RBAC role.
 *
 * <p>Name convention: {@code ROLE_ADMIN}, {@code ROLE_USER}.
 * Spring Security's {@code hasRole("ADMIN")} strips the "ROLE_" prefix automatically.</p>
 */
@Entity
@Table(
    name = "ROLES",
    uniqueConstraints = @UniqueConstraint(name = "UQ_ROLES_NAME", columnNames = "NAME")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq_gen")
    @SequenceGenerator(name = "roles_seq_gen", sequenceName = "SEQ_ROLES", allocationSize = 1)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    /**
     * Loaded EAGERLY so {@code CustomUserDetails} can collect all authorities
     * (roles + permissions) in a single shot.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "ROLE_PERMISSIONS",
        joinColumns        = @JoinColumn(name = "ROLE_ID"),
        inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}
