package com.learning.api_gateway.config;

import com.learning.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration
 * Demonstrates: Dynamic routing, Load balancing
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                // User Service Routes
                .route("user-service-auth", r -> r
                        .path("/api/v1/users/auth/**")
                        .uri("lb://user-service"))
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .circuitBreaker(c -> c
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user-service")))
                        .uri("lb://user-service"))

                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .circuitBreaker(c -> c
                                        .setName("orderServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/order-service")))
                        .uri("lb://order-service"))

                .build();
    }
}

