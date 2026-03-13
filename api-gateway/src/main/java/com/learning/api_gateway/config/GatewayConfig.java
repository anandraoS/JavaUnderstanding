package com.learning.api_gateway.config;

import com.learning.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * ═══════════════════════════════════════════════════════════════════
 * API GATEWAY ROUTE CONFIGURATION
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Dynamic routing, Load balancing, Filter chain, Circuit breaker
 *
 * CONCEPT — WHAT IS AN API GATEWAY?
 *   Without gateway: Client must know every service URL
 *     client → http://192.168.1.5:8081/users  (user-service)
 *     client → http://192.168.1.6:8082/orders (order-service)
 *
 *   With gateway: Client only knows ONE URL
 *     client → http://gateway:8080/api/v1/users  → routed to user-service
 *     client → http://gateway:8080/api/v1/orders → routed to order-service
 *
 * PSEUDOCODE — How routing works:
 *   1. Request arrives: GET /api/v1/users/123
 *   2. Gateway matches path: "/api/v1/users/**" → route "user-service"
 *   3. Gateway applies filters IN ORDER:
 *      a) JwtAuthenticationFilter → validates JWT, adds X-Username header
 *      b) CircuitBreaker → protects against user-service being down
 *   4. Gateway resolves URI: "lb://user-service"
 *      → "lb://" means load-balanced via Eureka
 *      → asks Eureka: "Where is user-service?"
 *      → gets: 192.168.1.5:8081
 *   5. Gateway forwards request to http://192.168.1.5:8081/api/v1/users/123
 *   6. If user-service is DOWN → circuit breaker redirects to:
 *      forward:/fallback/user-service → FallbackController returns error JSON
 *
 * PSEUDOCODE — Route ORDERING is critical:
 *   Routes are evaluated TOP to BOTTOM. The FIRST match wins.
 *   So we define SPECIFIC routes (auth, register) BEFORE generic ones.
 *
 *   /api/v1/users/auth/**   → NO JWT filter (public - login)
 *   POST /api/v1/users      → NO JWT filter (public - register)
 *   /api/v1/users/**        → JWT filter applied (protected)
 *   /api/v1/orders/**       → JWT filter applied (protected)
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()

                // ─── PUBLIC ROUTES (no JWT required) ─────────────────────────────

                // 1. Auth endpoints: login, register via /auth path
                .route("user-service-auth", r -> r
                        .path("/api/v1/users/auth/**")
                        .uri("lb://user-service"))

                // 2. User registration: POST /api/v1/users (no JWT needed)
                //    Must be BEFORE the generic /api/v1/users/** route!
                .route("user-service-register", r -> r
                        .path("/api/v1/users")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("lb://user-service"))

                // ─── PROTECTED ROUTES (JWT required) ─────────────────────────────

                // 3. All other user endpoints (GET, PUT, DELETE)
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .filter(jwtFilter)
                                .circuitBreaker(c -> c
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user-service")))
                        .uri("lb://user-service"))

                // 4. Order service — all endpoints protected
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
