package com.company.security.mapper;

import com.company.security.dto.UserDto;
import com.company.security.entity.Permission;
import com.company.security.entity.Role;
import com.company.security.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper — converts {@link User} → {@link UserDto}.
 * Replace with MapStruct if the mapping logic grows complex.
 */
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                permissions
        );
    }
}
