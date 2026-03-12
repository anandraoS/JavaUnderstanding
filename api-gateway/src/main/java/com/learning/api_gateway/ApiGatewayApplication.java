package com.learning.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * Demonstrates: API Gateway pattern, Routing, Load balancing, Security
 *
 * Key Concepts:
 * - Single entry point for all microservices
 * - Request routing and filtering
 * - JWT validation at gateway level (X-Username / X-Role forwarded to services)
 * - Circuit breaker integration
 * - Rate limiting (requires Redis — optional dependency)
 * - CORS configuration
 * - Load balancing through Eureka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
