package com.company.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Persisted refresh token.
 *
 * <p>Tokens are never deleted — they are revoked ({@code REVOKED = 1}) on logout or rotation.
 * This enables an audit trail and detection of token-reuse attacks.</p>
 */
@Entity
@Table(
    name = "REFRESH_TOKENS",
    uniqueConstraints = @UniqueConstraint(name = "UQ_REFRESH_TOKENS_TOKEN", columnNames = "TOKEN"),
    indexes = {
        @Index(name = "IDX_RT_TOKEN",      columnList = "TOKEN"),
        @Index(name = "IDX_RT_USER_ID",    columnList = "USER_ID"),
        @Index(name = "IDX_RT_EXPIRES_AT", columnList = "EXPIRES_AT")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_tokens_seq_gen")
    @SequenceGenerator(name = "refresh_tokens_seq_gen", sequenceName = "SEQ_REFRESH_TOKENS", allocationSize = 1)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    /** UUID-based opaque token value. */
    @Column(name = "TOKEN", nullable = false, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "EXPIRES_AT", nullable = false)
    private OffsetDateTime expiresAt;

    /** Set to {@code true} on logout or when a newer token has been issued. */
    @Column(name = "REVOKED", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
