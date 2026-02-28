package com.company.security.service;

import com.company.security.dto.*;
import com.company.security.entity.RefreshToken;
import com.company.security.entity.User;
import com.company.security.mapper.UserMapper;
import com.company.security.repository.UserRepository;
import com.company.security.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the authentication flows: login, register, token-refresh, logout.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService            jwtService;
    private final RefreshTokenService   refreshTokenService;
    private final UserService           userService;
    private final UserRepository        userRepository;
    private final UserMapper            userMapper;

    // ── Login ───────────────────────────────────────────────────────────────

    /**
     * Authenticates credentials via {@link CustomAuthenticationProvider},
     * then issues a new access token and refresh token.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsernameAndNotDeleted(principal.getUsername())
                .orElseThrow();                            // already validated above

        String accessToken = jwtService.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.of(
                accessToken,
                refreshToken.getToken(),
                jwtService.getAccessTokenExpiration(),
                userMapper.toDto(user)
        );
    }

    // ── Register ────────────────────────────────────────────────────────────

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        User user = userService.registerUser(request);
        CustomUserDetails principal = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.of(
                accessToken,
                refreshToken.getToken(),
                jwtService.getAccessTokenExpiration(),
                userMapper.toDto(user)
        );
    }

    // ── Token refresh ────────────────────────────────────────────────────────

    /**
     * Validates the refresh token, rotates it, and issues a new access token.
     */
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken existingToken = refreshTokenService.verifyAndGet(request.refreshToken());
        User user = existingToken.getUser();

        CustomUserDetails principal = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(principal);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return TokenRefreshResponse.of(
                newAccessToken,
                newRefreshToken.getToken(),
                jwtService.getAccessTokenExpiration()
        );
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    /** Revokes all refresh tokens for the authenticated user. */
    @Transactional
    public void logout(String username) {
        userRepository.findByUsernameAndNotDeleted(username)
                .ifPresent(refreshTokenService::revokeAllByUser);
    }
}
