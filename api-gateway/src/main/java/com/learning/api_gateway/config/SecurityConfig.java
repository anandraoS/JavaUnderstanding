package com.learning.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security Configuration for API Gateway (Reactive / WebFlux)
 * Demonstrates: Gateway-level security, JWT delegation to downstream services
 *
 * KEY CONCEPT:
 * The API Gateway does NOT authenticate users itself — it validates JWT tokens
 * and passes user info (X-Username, X-Role headers) to downstream services.
 * Spring Security here only prevents accessing internal endpoints.
 *
 * We provide a dummy ReactiveUserDetailsService to satisfy Spring Security's
 * autoconfiguration requirement (avoids "No ReactiveUserDetailsService" warning).
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
                .build();
    }

    /**
     * Dummy ReactiveUserDetailsService — required to suppress Spring Security
     * autoconfiguration warning. Gateway relies on JWT, not in-memory users.
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails dummy = User.withDefaultPasswordEncoder()
                .username("gateway")
                .password("gateway")
                .roles("GATEWAY")
                .build();
        return new MapReactiveUserDetailsService(dummy);
    }
}
