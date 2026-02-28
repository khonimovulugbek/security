package com.company.security.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Fine-grained RBAC permission.
 *
 * <p>Convention: {@code RESOURCE_ACTION} — e.g. {@code USERS_READ}, {@code ROLES_MANAGE}.
 * Use with {@code @PreAuthorize("hasAuthority('USERS_READ')")} on controller methods.</p>
 */
@Entity
@Table(
    name = "PERMISSIONS",
    uniqueConstraints = @UniqueConstraint(name = "UQ_PERMISSIONS_NAME", columnNames = "NAME")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissions_seq_gen")
    @SequenceGenerator(name = "permissions_seq_gen", sequenceName = "SEQ_PERMISSIONS", allocationSize = 1)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
