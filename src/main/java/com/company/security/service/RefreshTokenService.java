package com.company.security.service;

import com.company.security.entity.RefreshToken;
import com.company.security.entity.User;
import com.company.security.exception.TokenExpiredException;
import com.company.security.exception.TokenInvalidException;
import com.company.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Manages the refresh token lifecycle.
 *
 * <p>Token-rotation strategy: every call to {@link #createRefreshToken} revokes
 * all existing tokens for the user before issuing a new one.  This means a
 * compromised refresh token can only be used once before it is superseded.</p>
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;          // milliseconds

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Revokes all existing tokens for the user, then issues a new UUID token.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.revokeAllActiveByUser(user);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(OffsetDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    /**
     * Validates the raw token string and returns the entity if valid.
     *
     * @throws TokenInvalidException if the token is unknown or revoked
     * @throws TokenExpiredException if the token's TTL has elapsed
     */
    @Transactional(readOnly = true)
    public RefreshToken verifyAndGet(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new TokenInvalidException("Refresh token not found"));

        if (token.isRevoked()) {
            throw new TokenInvalidException("Refresh token has been revoked");
        }
        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired. Please log in again.");
        }

        return token;
    }

    /** Revokes all refresh tokens for the given user (called on logout). */
    @Transactional
    public void revokeAllByUser(User user) {
        refreshTokenRepository.revokeAllActiveByUser(user);
    }
}
