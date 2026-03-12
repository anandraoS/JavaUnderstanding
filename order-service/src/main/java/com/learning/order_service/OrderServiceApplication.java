package com.learning.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Order Service Application
 *
 * Demonstrates:
 * - Asynchronous message processing (Kafka, RabbitMQ)
 * - Circuit Breaker with Resilience4j
 * - Retry mechanisms
 * - Inter-service communication with WebClient
 * - Caching (in-memory fallback when Redis not available)
 * - Event-driven architecture
 * - H2 in-memory DB for local dev (no PostgreSQL needed)
 *
 * RabbitMQ/Redis: conditional beans handle availability gracefully.
 * No external infrastructure needed to start locally.
 */
@SpringBootApplication(
    scanBasePackages = {"com.learning.order_service", "com.learning.common_library"}
)
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
