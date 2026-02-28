package com.company.security.repository;

import com.company.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameAndDeletedFalse(String name);

    boolean existsByNameAndDeletedFalse(String name);
}
