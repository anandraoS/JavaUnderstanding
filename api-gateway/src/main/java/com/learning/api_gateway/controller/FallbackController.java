package com.learning.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════
 * FALLBACK CONTROLLER (Circuit Breaker Responses)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Graceful degradation, Fallback mechanism
 *
 * CONCEPT — WHAT HAPPENS WHEN A SERVICE IS DOWN?
 *   Without fallback:
 *     Client → Gateway → user-service (DOWN) → 504 Gateway Timeout (ugly)
 *
 *   With fallback:
 *     Client → Gateway → user-service (DOWN)
 *       → Circuit breaker trips
 *       → Redirects to: forward:/fallback/user-service
 *       → THIS controller handles it
 *       → Returns: { success: false, message: "Service temporarily unavailable" }
 *       → Client gets a NICE error response instantly (not a timeout)
 *
 * PSEUDOCODE — Flow:
 *   GatewayConfig defines: .setFallbackUri("forward:/fallback/user-service")
 *   When circuit breaker is OPEN → gateway forwards to this controller
 *   This controller returns a friendly JSON error with HTTP 503
 *
 * NOTE: This uses Mono<ResponseEntity> because gateway is REACTIVE (WebFlux)
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "User service is temporarily unavailable. Please try again later.");
        response.put("errorCode", "SERVICE_UNAVAILABLE");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/order-service")
    public Mono<ResponseEntity<Map<String, Object>>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Order service is temporarily unavailable. Please try again later.");
        response.put("errorCode", "SERVICE_UNAVAILABLE");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
