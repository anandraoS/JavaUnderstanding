package com.learning.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════
 * USER ENTITY — Maps to "users" table in PostgreSQL
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: JPA entity mapping, Lombok annotations, Bean lifecycle
 *
 * CONCEPT — WHAT IS AN ENTITY?
 *   Entity = Java class that maps to a DATABASE TABLE
 *   Each field = a COLUMN in the table
 *   Each instance = a ROW in the table
 *
 * PSEUDOCODE — How JPA works:
 *   Java:  User user = User.builder().username("john").email("j@x.com").build();
 *          userRepository.save(user);
 *   SQL:   INSERT INTO users (username, email, password, ...) VALUES ('john', 'j@x.com', ...);
 *
 *   Java:  userRepository.findById(1L);
 *   SQL:   SELECT * FROM users WHERE id = 1;
 *
 * PSEUDOCODE — Hibernate DDL-AUTO modes:
 *   create      → DROP table, CREATE fresh (data lost every restart)
 *   create-drop → CREATE on start, DROP on shutdown
 *   update      → ADD new columns, KEEP existing data (what we use)
 *   validate    → only CHECK schema matches, no changes
 *   none        → do nothing (production with Flyway/Liquibase)
 *
 * LOMBOK ANNOTATIONS EXPLAINED:
 *   @Data → generates getters, setters, toString, equals, hashCode
 *   @Builder → generates User.builder().username("x").build() pattern
 *   @NoArgsConstructor → generates empty constructor (required by JPA)
 *   @AllArgsConstructor → generates constructor with all fields
 *
 * JPA ANNOTATIONS EXPLAINED:
 *   @Entity → this class maps to a DB table
 *   @Table(name = "users") → table name (defaults to class name)
 *   @Id → primary key column
 *   @GeneratedValue(IDENTITY) → auto-increment (DB generates ID)
 *   @Column(unique = true) → adds UNIQUE constraint in DB
 *   @CreationTimestamp → auto-set current time on INSERT
 *   @UpdateTimestamp → auto-set current time on UPDATE
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
