package com.company.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Returns a structured JSON {@code 401 Unauthorized} response when an
 * unauthenticated request reaches a protected endpoint.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // LinkedHashMap preserves insertion order in the JSON output
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", "Authentication required: " + authException.getMessage());
        body.put("path", request.getServletPath());
        body.put("timestamp", OffsetDateTime.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
