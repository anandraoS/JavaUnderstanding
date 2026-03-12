package com.learning.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Order Service Application
 *
 * Demonstrates ALL enterprise concepts:
 * - Asynchronous message processing with Kafka AND RabbitMQ
 * - Circuit Breaker pattern with Resilience4j
 * - Retry mechanisms with exponential backoff
 * - Inter-service communication with WebClient (load-balanced)
 * - Distributed Caching with Redis
 * - Event-driven architecture (Kafka producer + consumer)
 * - Message queuing with RabbitMQ (exchanges, queues, bindings)
 * - PostgreSQL database with Spring Data JPA
 * - Distributed tracing with Zipkin
 */
@SpringBootApplication(
    scanBasePackages = {"com.learning.order_service", "com.learning.common_library"}
)
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableKafka
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
