package com.learning.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Audit Entity - Secondary Database
 * Demonstrates: Multiple datasource configuration
 */
@Entity
@Table(name = "user_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 50)
    private String username;

    @Column(length = 20)
    private String action; // CREATED, UPDATED, DELETED

    @Column(length = 500)
    private String details;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;
}

