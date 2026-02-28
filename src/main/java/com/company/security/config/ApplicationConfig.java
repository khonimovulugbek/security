package com.company.security.config;

import com.company.security.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * General application beans: password encoder and authentication manager.
 *
 * <p>Kept separate from {@link SecurityConfig} so that the beans can be
 * injected without triggering a circular dependency through the filter chain.</p>
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final CustomAuthenticationProvider authenticationProvider;

    /**
     * BCrypt with cost 12 — balances security strength and login latency.
     * Cost 10 is acceptable for high-traffic systems; increase to 14+ for very
     * sensitive data where login latency is not critical.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Wraps the custom provider in a {@link ProviderManager}.
     * Additional providers (LDAP, OAuth, etc.) can be added to the list.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider));
    }
}
