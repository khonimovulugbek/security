package com.company.security.service;

import com.company.security.dto.RegisterRequest;
import com.company.security.dto.UserDto;
import com.company.security.entity.User;
import com.company.security.exception.UserNotFoundException;
import com.company.security.mapper.UserMapper;
import com.company.security.repository.RoleRepository;
import com.company.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper      userMapper;

    /**
     * Registers a new user with the default {@code ROLE_USER} role.
     *
     * @throws IllegalArgumentException if the username or email is already taken
     * @throws IllegalStateException    if the ROLE_USER seed data is missing
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsernameAndDeletedFalse(request.username())) {
            throw new IllegalArgumentException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }

        var defaultRole = roleRepository.findByNameAndDeletedFalse("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role ROLE_USER not found — verify Liquibase seed data."));

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new HashSet<>(Set.of(defaultRole)))
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    /**
     * Soft-deletes a user by setting {@code deleted = true}.
     * Associated refresh tokens are handled by ON DELETE CASCADE on the DB side.
     */
    @Transactional
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        user.setDeleted(true);
        userRepository.save(user);
    }
}
