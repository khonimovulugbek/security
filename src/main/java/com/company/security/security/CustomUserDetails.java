package com.company.security.security;

import com.company.security.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring Security {@link UserDetails} adapter that wraps a {@link User} entity.
 *
 * <p>Authorities are built from <em>both</em> roles and permissions so that
 * {@code hasRole("ADMIN")} and {@code hasAuthority("USERS_WRITE")} work correctly.</p>
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id                    = user.getId();
        this.username              = user.getUsername();
        this.email                 = user.getEmail();
        this.password              = user.getPassword();
        this.enabled               = user.isEnabled();
        this.accountNonExpired     = user.isAccountNonExpired();
        this.accountNonLocked      = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.authorities           = buildAuthorities(user);
    }

    /**
     * Combines role names (e.g. {@code ROLE_ADMIN}) and permission names
     * (e.g. {@code USERS_READ}) into a flat set of {@link GrantedAuthority}.
     */
    private static Set<GrantedAuthority> buildAuthorities(User user) {
        Set<GrantedAuthority> auths = new HashSet<>();
        user.getRoles().forEach(role -> {
            auths.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(perm ->
                auths.add(new SimpleGrantedAuthority(perm.getName()))
            );
        });
        return auths;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities()  { return authorities;           }
    @Override public String  getPassword()                                      { return password;              }
    @Override public String  getUsername()                                      { return username;              }
    @Override public boolean isAccountNonExpired()                              { return accountNonExpired;     }
    @Override public boolean isAccountNonLocked()                               { return accountNonLocked;      }
    @Override public boolean isCredentialsNonExpired()                          { return credentialsNonExpired; }
    @Override public boolean isEnabled()                                        { return enabled;               }
}
