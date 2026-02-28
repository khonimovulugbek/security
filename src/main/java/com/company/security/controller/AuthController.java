package com.company.security.controller;

import com.company.security.dto.*;
import com.company.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST controller.
 *
 * <pre>
 * POST /api/v1/auth/register  — create account and receive tokens
 * POST /api/v1/auth/login     — authenticate and receive tokens
 * POST /api/v1/auth/refresh   — rotate refresh token and get new access token
 * POST /api/v1/auth/logout    — revoke all refresh tokens for the current user
 * </pre>
 *
 * All endpoints return the {@link ApiResponse} envelope.
 * Login / register endpoints are public (see {@code SecurityConfig}).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * <pre>
     * POST /api/v1/auth/register
     * {
     *   "username"  : "jane.doe",
     *   "email"     : "jane@example.com",
     *   "password"  : "Secure!123",
     *   "firstName" : "Jane",
     *   "lastName"  : "Doe"
     * }
     * </pre>
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        LoginResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /**
     * Authenticate with username + password.
     *
     * <pre>
     * POST /api/v1/auth/login
     * { "username": "admin", "password": "Admin@1234!" }
     *
     * 200 OK
     * {
     *   "success"  : true,
     *   "message"  : "Login successful",
     *   "data": {
     *     "accessToken"  : "eyJhbGciOiJIUzI1NiJ9...",
     *     "refreshToken" : "550e8400-e29b-41d4-a716-446655440000",
     *     "tokenType"    : "Bearer",
     *     "expiresIn"    : 3600000,
     *     "user"         : { "id": 1, "username": "admin", "roles": ["ROLE_ADMIN"], ... }
     *   },
     *   "timestamp": "2025-02-28T10:15:30+05:00"
     * }
     * </pre>
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Exchange a valid refresh token for a new access token (token rotation).
     *
     * <pre>
     * POST /api/v1/auth/refresh
     * { "refreshToken": "550e8400-e29b-41d4-a716-446655440000" }
     * </pre>
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request) {

        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    /**
     * Revoke all refresh tokens for the currently authenticated user.
     * The client is responsible for discarding the access token locally.
     *
     * <pre>
     * POST /api/v1/auth/logout
     * Authorization: Bearer &lt;access_token&gt;
     * </pre>
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
