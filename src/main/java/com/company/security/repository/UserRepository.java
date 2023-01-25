package com.company.security.repository;

import com.company.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author : Khonimov Ulugbek
 * Date : 1/18/2023
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
