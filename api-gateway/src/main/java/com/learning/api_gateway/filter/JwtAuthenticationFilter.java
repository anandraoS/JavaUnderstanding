package com.learning.api_gateway.filter;

import com.learning.common_library.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ═══════════════════════════════════════════════════════════════════
 * JWT AUTHENTICATION FILTER (Gateway-level)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Gateway-level security, JWT validation, Request mutation
 *
 * CONCEPT — WHY VALIDATE JWT AT THE GATEWAY?
 *   Without: Every microservice validates JWT independently (code duplication)
 *   With:    Gateway validates ONCE, adds user info as headers
 *            → downstream services trust X-Username / X-Role headers
 *
 * PSEUDOCODE — Complete request flow:
 *   1. Client sends: GET /api/v1/orders
 *      Header: Authorization: Bearer eyJhbGciOiJIUzUxMi...
 *
 *   2. Gateway intercepts → JwtAuthenticationFilter.filter() runs
 *
 *   3. Is URL public? ("/auth/", "/actuator/") → skip validation → pass through
 *
 *   4. Extract token: "Bearer eyJhbGci..." → "eyJhbGci..."
 *
 *   5. Validate token via JwtUtil:
 *      → Decode JWT → check signature with secret key
 *      → Check expiration: isExpired? → 401 Unauthorized
 *      → Extract username: "john_doe"
 *      → Extract role: "ROLE_USER"
 *
 *   6. Mutate request (add headers for downstream service):
 *      ServerHttpRequest modified = request.mutate()
 *          .header("X-Username", "john_doe")
 *          .header("X-Role", "ROLE_USER")
 *          .build();
 *
 *   7. Forward to order-service → order-service reads X-Username header
 *      → knows WHO is making the request without re-validating JWT
 *
 * NOTE: This is a GatewayFilter (reactive/WebFlux), NOT a Servlet filter.
 *   Uses: ServerWebExchange, ServerHttpRequest, Mono<Void>
 *   NOT:  HttpServletRequest, HttpServletResponse
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // Validate token
            String username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token, username)) {
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Add user info to request headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Username", username)
                    .header("X-Role", jwtUtil.extractRole(token))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/auth/") ||
               path.contains("/actuator/") ||
               path.contains("/swagger-ui") ||
               path.contains("/api-docs");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Authentication error: {}", err);
        return response.setComplete();
    }
}
