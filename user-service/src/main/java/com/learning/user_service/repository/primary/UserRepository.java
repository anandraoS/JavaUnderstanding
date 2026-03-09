package com.learning.user_service.repository.primary;

import com.learning.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository - Primary Database
 * Demonstrates: Repository pattern, CRUD operations, Custom queries, Pagination
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByActiveTrue(Pageable pageable);

    // Custom query using JPQL
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true")
    Page<User> findActiveUsersByRole(@Param("role") String role, Pageable pageable);

    // Native query example
    @Query(value = "SELECT * FROM users WHERE first_name LIKE %:name% OR last_name LIKE %:name%",
           nativeQuery = true)
    Page<User> searchByName(@Param("name") String name, Pageable pageable);
}

