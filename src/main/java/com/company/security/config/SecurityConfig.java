package com.company.security.config;

import com.company.security.exception.handler.JwtAccessDeniedHandler;
import com.company.security.exception.handler.JwtAuthenticationEntryPoint;
import com.company.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 filter-chain configuration.
 *
 * <ul>
 *   <li>CSRF disabled — not needed for stateless JWT APIs.</li>
 *   <li>Session creation policy STATELESS — no HTTP session is created or used.</li>
 *   <li>JWT filter runs before the standard username/password filter.</li>
 *   <li>{@code @EnableMethodSecurity} activates {@code @PreAuthorize} / {@code @PostAuthorize}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter    jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAccessDeniedHandler     accessDeniedHandler;

    /** Endpoints that do not require a valid JWT. */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",         // login, register, refresh
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // ── Disable CSRF (stateless REST API) ──────────────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── Stateless session ───────────────────────────────────────────
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Authorization rules ─────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // CORS preflight
                        .anyRequest().authenticated()
                )

                // ── Custom 401 / 403 handlers ───────────────────────────────────
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // ── JWT filter ──────────────────────────────────────────────────
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
