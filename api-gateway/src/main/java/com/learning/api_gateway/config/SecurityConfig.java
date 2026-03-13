package com.learning.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security Configuration for API Gateway (Reactive / WebFlux)
 * Demonstrates: Gateway-level security, JWT delegation to downstream services
 *
 * KEY CONCEPT:
 * The API Gateway does NOT authenticate users itself — it validates JWT tokens
 * via a custom GatewayFilter (JwtAuthenticationFilter) and passes user info
 * (X-Username, X-Role headers) to downstream services.
 *
 * Spring Security here is configured to PERMIT ALL exchanges so that it does
 * NOT interfere with the JWT validation done by the gateway filter.
 * We disable httpBasic, formLogin, logout, and anonymous to ensure Spring Security
 * does not try to process the Authorization header — that's the gateway filter's job.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Auth endpoints — always public
                        .pathMatchers("/api/v1/users/auth/**").permitAll()
                        // Registration endpoint — POST /api/v1/users — always public
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/users").permitAll()
                        // Login/Register direct paths — always public
                        .pathMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()
                        // Actuator — public for health checks
                        .pathMatchers("/actuator/**").permitAll()
                        // Fallback endpoints — public
                        .pathMatchers("/fallback/**").permitAll()
                        // Swagger / OpenAPI — public
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        // All other requests pass through (JWT filter in route handles auth)
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // Disable logout filter — not needed for stateless JWT
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                // Disable anonymous authentication — let requests pass through as-is
                // so the Gateway JWT filter handles authentication, not Spring Security
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .build();
    }
}
