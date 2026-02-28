package com.company.security.dto;

/**
 * Returned by POST /api/v1/auth/login and POST /api/v1/auth/register.
 *
 * <pre>
 * {
 *   "accessToken"  : "eyJhbGciOiJIUzI1NiJ9...",
 *   "refreshToken" : "550e8400-e29b-41d4-a716-446655440000",
 *   "tokenType"    : "Bearer",
 *   "expiresIn"    : 3600000,
 *   "user"         : { ... }
 * }
 * </pre>
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserDto user
) {
    public static LoginResponse of(String accessToken, String refreshToken,
                                   long expiresIn, UserDto user) {
        return new LoginResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
