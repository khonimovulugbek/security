package com.company.security.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(

        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken
) {}
