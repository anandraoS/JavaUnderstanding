package com.learning.order_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ═══════════════════════════════════════════════════════════════════
 * WEBCLIENT CONFIGURATION (Inter-Service Communication)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: REST client, Load balancing via Eureka
 *
 * CONCEPT — WHY WEBCLIENT OVER RESTTEMPLATE?
 *   RestTemplate: blocking (thread waits for response) — DEPRECATED
 *   WebClient: non-blocking (thread freed while waiting) — MODERN
 *
 * PSEUDOCODE — How @LoadBalanced works:
 *   Without @LoadBalanced:
 *     webClient.get("http://user-service/users/1")
 *     → tries to connect to host "user-service" → DNS FAILURE!
 *
 *   With @LoadBalanced:
 *     webClient.get("http://user-service/users/1")
 *     → Spring intercepts → asks Eureka: "Where is user-service?"
 *     → Eureka responds: "192.168.1.5:8081, 192.168.1.6:8081"
 *     → Spring picks one (round-robin) → sends to 192.168.1.5:8081/users/1
 *
 * PSEUDOCODE — Usage in OrderService:
 *   UserDTO user = webClientBuilder.build()
 *       .get()
 *       .uri("http://user-service/api/v1/users/" + userId)
 *       .retrieve()
 *       .bodyToMono(UserDTO.class)  // Returns Mono (reactive)
 *       .block();                    // Blocks to get value (sync call)
 */
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
