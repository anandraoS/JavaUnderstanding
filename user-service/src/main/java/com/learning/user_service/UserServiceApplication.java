package com.learning.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * User Service Application
 *
 * Demonstrates:
 * - RESTful API development with Spring MVC
 * - Spring Data JPA with multiple datasources (H2 locally, PostgreSQL in prod)
 * - Spring Security with JWT authentication
 * - Caching (in-memory fallback when Redis not available)
 * - Async processing
 * - Event-driven architecture with Kafka
 * - Service discovery with Eureka
 * - Actuator for monitoring
 */
@SpringBootApplication(
    scanBasePackages = {"com.learning.user_service", "com.learning.common_library"}
)
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableScheduling
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
