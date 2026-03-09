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
 * Fallback Controller for Circuit Breaker
 * Demonstrates: Graceful degradation, Fallback mechanisms
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

