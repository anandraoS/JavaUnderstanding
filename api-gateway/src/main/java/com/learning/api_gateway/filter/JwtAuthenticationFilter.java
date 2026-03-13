package com.learning.api_gateway.filter;

import com.learning.common_library.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
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

        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().toString(), request.getMethod() != null ? request.getMethod().name() : null)) {
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null) {
            log.debug("No Authorization header present for request {} {}", request.getMethod(), request.getPath());
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header does not start with Bearer for request {} {}", request.getMethod(), request.getPath());
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        // Redact token for logs (only show first/last 4 chars)
        String redacted = token.length() > 8 ? token.substring(0, 4) + "..." + token.substring(token.length() - 4) : "(redacted)";
        log.debug("Received Bearer token (redacted): {} for {} {}", redacted, request.getMethod(), request.getPath());

        try {
            // Extract username and validate
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                log.warn("JWT token has no subject (username) for request {} {}", request.getMethod(), request.getPath());
                return onError(exchange, "JWT token missing subject", HttpStatus.UNAUTHORIZED);
            }

            boolean valid = jwtUtil.validateToken(token, username);
            if (!valid) {
                log.warn("JWT validation returned false for user '{}' on request {} {}", username, request.getMethod(), request.getPath());
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Add user info to request headers for downstream services.
            // Some ServerHttpRequest implementations expose read-only headers, so
            // using request.mutate().header(...) can throw UnsupportedOperationException.
            // Workaround: create a ServerHttpRequestDecorator that returns a new HttpHeaders
            // containing all original headers plus the X-Username / X-Role entries.
            HttpHeaders newHeaders = new HttpHeaders();
            newHeaders.putAll(request.getHeaders());
            newHeaders.add("X-Username", username);
            String role = jwtUtil.extractRole(token);
            if (role != null) newHeaders.add("X-Role", role);

            ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
                @Override
                public HttpHeaders getHeaders() {
                    return newHeaders;
                }
            };

            return chain.filter(exchange.mutate().request(decoratedRequest).build());

        } catch (Exception e) {
            // Log full stacktrace at debug level for diagnosis, but return minimal message to client
            log.debug("Exception during JWT validation", e);
            String cause = e.getClass().getSimpleName() + (e.getMessage() != null ? ": " + e.getMessage() : "");
            return onError(exchange, "JWT validation failed: " + cause, HttpStatus.UNAUTHORIZED);
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
        // Normalize null
        if (path == null) {
            return false;
        }

        // Treat any path starting with /api/v1/users as public for POST (registration)
        if (path.startsWith("/api/v1/users")) {
            if ("POST".equalsIgnoreCase(method)) {
                log.debug("Skipping JWT validation for public POST user endpoint: {}", path);
                return true;
            }
        }

        // Other public paths
        boolean publicPaths = path.contains("/auth/") ||
                path.contains("/actuator") ||
                path.contains("/swagger-ui") ||
                path.contains("/api-docs") ||
                path.startsWith("/fallback");

        if (publicPaths) {
            log.debug("Skipping JWT validation for public path: {}", path);
        }
        return publicPaths;
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
