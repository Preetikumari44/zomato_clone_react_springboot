package com.novabyte.zomatoclone.user.repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(COALESCE(u.fullName, '')) LIKE :keywordPattern " +
           "OR LOWER(COALESCE(u.email, '')) LIKE :keywordPattern")
    Page<User> search(@Param("keywordPattern") String keywordPattern, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.role = :role")
    long countByRole(@Param("role") Role role);
}
