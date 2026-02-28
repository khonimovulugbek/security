package com.company.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Application user.
 *
 * <p>Column PASSWORD_HASH stores a BCrypt-encoded password (never plain-text).
 * All Spring Security account-status flags are persisted as NUMBER(1) in Oracle.</p>
 */
@Entity
@Table(
    name = "USERS",
    indexes = {
        @Index(name = "IDX_USERS_USERNAME", columnList = "USERNAME"),
        @Index(name = "IDX_USERS_EMAIL",    columnList = "EMAIL"),
        @Index(name = "IDX_USERS_DELETED",  columnList = "DELETED")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_USERS_USERNAME", columnNames = "USERNAME"),
        @UniqueConstraint(name = "UQ_USERS_EMAIL",    columnNames = "EMAIL")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(name = "users_seq_gen", sequenceName = "SEQ_USERS", allocationSize = 1)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    /** BCrypt-encoded password — never exposed in API responses. */
    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String password;

    @Column(name = "FIRST_NAME", length = 100)
    private String firstName;

    @Column(name = "LAST_NAME", length = 100)
    private String lastName;

    @Column(name = "ENABLED", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "ACCOUNT_NON_EXPIRED", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "ACCOUNT_NON_LOCKED", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "CREDENTIALS_NON_EXPIRED", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;

    /**
     * Roles loaded EAGERLY because Spring Security needs all granted authorities
     * at authentication time (inside {@code CustomUserDetails}).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns        = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
