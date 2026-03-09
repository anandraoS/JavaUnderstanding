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
 * Global Logging Filter
 * Demonstrates: Request/Response logging, Correlation ID
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate correlation ID if not present
        String correlationId = request.getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        log.info("Incoming request: {} {} | Correlation-ID: {}",
                request.getMethod(),
                request.getPath(),
                correlationId);

        // Add correlation ID to request for downstream services
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Correlation-ID", correlationId)
                .build();

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Request completed: {} {} | Status: {} | Duration: {}ms | Correlation-ID: {}",
                            request.getMethod(),
                            request.getPath(),
                            exchange.getResponse().getStatusCode(),
                            duration,
                            correlationId);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

