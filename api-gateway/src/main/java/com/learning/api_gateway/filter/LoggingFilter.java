package com.learning.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * ═══════════════════════════════════════════════════════════════════
 * GLOBAL LOGGING FILTER (applies to ALL requests)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Request/Response logging, Correlation ID, GlobalFilter
 *
 * CONCEPT — WHAT IS A GLOBAL FILTER?
 *   Route filter: applies to specific routes (e.g., JwtAuthenticationFilter)
 *   Global filter: applies to ALL requests (logging, timing, headers)
 *
 * CONCEPT — CORRELATION ID:
 *   In microservices, one user request touches 5+ services.
 *   How to trace "which log line belongs to which request"?
 *   → Assign a UNIQUE ID to the request at the gateway
 *   → Pass it as X-Correlation-ID header to ALL downstream services
 *   → Every log line includes this ID
 *   → grep "abc-123" in logs → see the ENTIRE journey
 *
 * PSEUDOCODE — Filter flow:
 *   1. Request arrives at gateway
 *   2. Has X-Correlation-ID header? → use it
 *      No? → generate UUID: "abc-123-def-456"
 *   3. Log: "Incoming: GET /api/v1/users | Correlation-ID: abc-123"
 *   4. Add X-Correlation-ID to request headers
 *   5. Forward request → downstream service processes it
 *   6. Response comes back
 *   7. Log: "Completed: GET /api/v1/users | Status: 200 | Duration: 45ms"
 *
 * CONCEPT — Ordered.HIGHEST_PRECEDENCE:
 *   getOrder() returns the LOWEST number = runs FIRST
 *   This filter runs BEFORE JwtAuthenticationFilter
 *   So every request is logged, even if JWT validation fails
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate correlation ID if not present
        String headerCorrelationId = request.getHeaders().getFirst("X-Correlation-ID");
        String correlationId;
        ServerWebExchange mutatedExchange = exchange;
        if (headerCorrelationId == null || headerCorrelationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            // Only mutate and add header if it was missing
            try {
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Correlation-ID", correlationId)
                        .build();
                mutatedExchange = exchange.mutate().request(modifiedRequest).build();
            } catch (UnsupportedOperationException e) {
                // If headers are read-only, skip mutation and just use the original exchange
                mutatedExchange = exchange;
            }
        } else {
            correlationId = headerCorrelationId;
        }

        log.info("Incoming request: {} {} | Correlation-ID: {}",
                request.getMethod(),
                request.getPath(),
                correlationId);

        long startTime = System.currentTimeMillis();

        final String finalCorrelationId = correlationId;
        final ServerHttpRequest finalRequest = request;
        final ServerWebExchange finalExchange = mutatedExchange;
        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Request completed: {} {} | Status: {} | Duration: {}ms | Correlation-ID: {}",
                            finalRequest.getMethod(),
                            finalRequest.getPath(),
                            finalExchange.getResponse().getStatusCode(),
                            duration,
                            finalCorrelationId);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
