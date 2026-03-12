package com.learning.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * User Service Application
 *
 * Demonstrates ALL enterprise concepts:
 * - RESTful API with Spring MVC
 * - Reactive Programming with WebFlux (Mono/Flux)
 * - Spring Data JPA with MULTIPLE datasources (PostgreSQL + MySQL)
 * - Spring Security with JWT authentication
 * - Distributed Caching with Redis (@Cacheable, @CacheEvict)
 * - Async processing with @Async
 * - Event-driven architecture with Apache Kafka
 * - Scheduled tasks with @Scheduled (Cron expressions)
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 * - Monitoring with Actuator + Prometheus
 * - OpenAPI / Swagger documentation
 */
@SpringBootApplication(
    scanBasePackages = {"com.learning.user_service", "com.learning.common_library"}
)
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableKafka
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
