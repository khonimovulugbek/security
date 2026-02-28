package com.company.security.dto;

import java.util.Set;

/**
 * Serializable user projection — safe to include in API responses.
 * Deliberately omits the password hash.
 */
public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        Set<String> permissions
) {}
