package com.company.security.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Custom {@link AuthenticationProvider} that delegates credential verification
 * to {@link CustomUserDetailsService} and BCrypt password matching.
 *
 * <p>Checks all Spring Security account-status flags in order:</p>
 * <ol>
 *   <li>Account enabled</li>
 *   <li>Account not locked</li>
 *   <li>Account not expired</li>
 *   <li>Credentials not expired</li>
 *   <li>Password match</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("Account is locked");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new BadCredentialsException("Account has expired");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new BadCredentialsException("Credentials have expired");
        }
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,                          // credentials cleared after auth
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
