package com.company.security.service;

import com.company.security.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Handles JWT creation, validation, and claim extraction.
 *
 * <p>Uses JJWT 0.12.x API (HMAC-SHA-256 / HS256).</p>
 *
 * <h3>Example Access-Token Payload</h3>
 * <pre>
 * {
 *   "sub"         : "admin",
 *   "iat"         : 1709200000,
 *   "exp"         : 1709203600,
 *   "userId"      : 1,
 *   "email"       : "admin@company.com",
 *   "authorities" : ["ROLE_ADMIN", "USERS_READ", "USERS_WRITE", ...]
 * }
 * </pre>
 */
@Service
@Slf4j
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    // ── Key derivation ──────────────────────────────────────────────────────

    private SecretKey getSigningKey() {
        // Derive a 256-bit HMAC key directly from the UTF-8 bytes of the secret.
        // Ensure the secret is at least 32 characters (256 bits).
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ── Token generation ────────────────────────────────────────────────────

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof CustomUserDetails custom) {
            claims.put("userId", custom.getId());
            claims.put("email",  custom.getEmail());
            List<String> authorities = custom.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            claims.put("authorities", authorities);
        }

        return buildToken(claims, userDetails, accessTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Token validation ────────────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ── Claim extraction ────────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Accessors ───────────────────────────────────────────────────────────

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
