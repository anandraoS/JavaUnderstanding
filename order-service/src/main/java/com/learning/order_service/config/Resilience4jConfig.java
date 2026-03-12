package com.learning.order_service.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * ═══════════════════════════════════════════════════════════════════
 * RESILIENCE4J CIRCUIT BREAKER CONFIGURATION
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Circuit breaker pattern, Fault tolerance
 *
 * CONCEPT — THE PROBLEM:
 *   Order-Service calls User-Service via HTTP.
 *   User-Service goes DOWN.
 *   Without circuit breaker:
 *     → Each call waits 30 seconds for timeout
 *     → 100 users = 100 threads blocked = OUT OF MEMORY
 *     → Order-Service ALSO goes down = CASCADE FAILURE
 *
 * CONCEPT — THE SOLUTION (Circuit Breaker):
 *   Like an electrical circuit breaker in your house:
 *     Overload? → breaker OPENS → cuts power → prevents fire
 *
 * PSEUDOCODE — Three states:
 *   CLOSED (normal):
 *     All requests pass through → monitoring failure rate
 *     failureRate < 50% → stay CLOSED
 *     failureRate >= 50% → switch to OPEN
 *
 *   OPEN (protecting):
 *     ALL requests immediately return FALLBACK response
 *     No actual HTTP call made → instant response (< 1ms)
 *     After waitDuration (10 seconds) → switch to HALF_OPEN
 *
 *   HALF_OPEN (testing):
 *     Allow 3 test requests through
 *     If they succeed → back to CLOSED (service recovered!)
 *     If they fail → back to OPEN (service still down)
 *
 * PSEUDOCODE — How it works in code:
 *   @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
 *   public void validateUser(Long userId) {
 *       webClient.get("http://user-service/users/" + userId)  // MAY FAIL
 *   }
 *
 *   public void validateUserFallback(Long userId, Exception ex) {
 *       // This runs INSTEAD when circuit is OPEN
 *       // Return cached data, or throw friendly error
 *   }
 *
 * PSEUDOCODE — Retry pattern:
 *   @Retry(name = "userService")
 *   attempt 1: FAIL → wait 1 second
 *   attempt 2: FAIL → wait 2 seconds (exponential backoff)
 *   attempt 3: FAIL → give up → circuit breaker counts this as failure
 */
@Configuration
public class Resilience4jConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .minimumNumberOfCalls(5)
                        .build())
                .build());
    }
}
