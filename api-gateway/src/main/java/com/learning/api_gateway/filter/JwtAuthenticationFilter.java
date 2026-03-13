package com.learning.api_gateway.filter;

import com.learning.common_library.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

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
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        log.info("JwtAuthenticationFilter initialized with JwtUtil: {}", jwtUtil != null ? "OK" : "NULL!");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod() != null ? request.getMethod().name() : null;

        log.debug("JWT Filter — path: {} method: {}", path, method);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path, method)) {
            log.debug("JWT Filter — public endpoint, skipping: {}", path);
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JWT Filter — missing or invalid Authorization header for: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        // Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            log.warn("JWT Filter — empty token for: {}", path);
            return onError(exchange, "Empty JWT token", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Step 1: Extract username from token
            String username = jwtUtil.extractUsername(token);
            log.debug("JWT Filter — extracted username: {}", username);

            if (username == null) {
                log.warn("JWT Filter — could not extract username from token");
                return onError(exchange, "Could not extract username from token", HttpStatus.UNAUTHORIZED);
            }

            // Step 2: Validate token (signature + expiration)
            if (!jwtUtil.validateToken(token, username)) {
                log.warn("JWT Filter — token validation failed for user: {}", username);
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Step 3: Extract role
            String role = jwtUtil.extractRole(token);
            log.debug("JWT Filter — validated user: {} role: {}", username, role);

            // Step 4: Add user info as headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Username", username)
                    .header("X-Role", role != null ? role : "ROLE_USER")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT Filter — token expired: {}", e.getMessage());
            return onError(exchange, "JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.warn("JWT Filter — invalid signature: {}", e.getMessage());
            return onError(exchange, "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("JWT Filter — malformed token: {}", e.getMessage());
            return onError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("JWT Filter — unexpected error: {} ({})", e.getMessage(), e.getClass().getSimpleName());
            return onError(exchange, "JWT validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * PSEUDOCODE — Public endpoint matching:
     *   Check if the request path is a public endpoint that doesn't need JWT.
     *   This is a SAFETY NET — these paths shouldn't even reach this filter
     *   because GatewayConfig defines separate routes without the JWT filter.
     *   But if routing changes, this prevents accidental 401s on public endpoints.
     */
    private boolean isPublicEndpoint(String path, String method) {
        return path.contains("/auth/") ||
               path.contains("/actuator") ||
               path.contains("/swagger-ui") ||
               path.contains("/api-docs") ||
               path.startsWith("/fallback") ||
               (path.equals("/api/v1/users") && "POST".equalsIgnoreCase(method)) ||
               path.equals("/api/v1/users/register") ||
               path.equals("/api/v1/users/login");
    }

    /**
     * Return a JSON error response instead of an empty body.
     * This helps clients understand what went wrong.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"message\":\"%s\",\"errorCode\":\"AUTHENTICATION_FAILED\"}",
                message
        );

        log.error("Authentication error: {} — returning {}", message, httpStatus);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
