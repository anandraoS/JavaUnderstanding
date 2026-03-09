package com.learning.user_service.repository.secondary;

import com.learning.user_service.entity.UserAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User Audit Repository - Secondary Database
 * Demonstrates: Multiple datasource usage
 */
@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit, Long> {

    List<UserAudit> findByUserId(Long userId);

    List<UserAudit> findByUsername(String username);
}

