package com.learning.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ═══════════════════════════════════════════════════════════════════
 * SPRING SECURITY CONFIGURATION (MVC / Servlet-based)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Spring Security, Stateless JWT auth, Method-level security
 *
 * CONCEPT — HOW SPRING SECURITY WORKS:
 *   Every HTTP request passes through a FILTER CHAIN before reaching your controller.
 *   Spring Security adds filters to this chain that check authentication.
 *
 * PSEUDOCODE — Request flow:
 *   Client sends: POST /api/v1/users { body }
 *     → SecurityFilterChain intercepts
 *     → Is this URL in permitAll()? → YES → pass through to controller
 *     → Is this URL in authenticated()? → check JWT token
 *       → Token valid? → allow through
 *       → Token invalid/missing? → return 401 Unauthorized
 *
 * PSEUDOCODE — Why STATELESS session:
 *   Traditional: Server stores session in memory (Session ID in cookie)
 *   JWT/Stateless: Server stores NOTHING. Token contains all info.
 *     → Server can be restarted without losing logins
 *     → Multiple servers can handle same user (no sticky sessions)
 *     → Perfect for microservices (each service is independent)
 *
 * PSEUDOCODE — PasswordEncoder (BCrypt):
 *   password = "myPassword123"
 *   encoded = BCrypt.encode(password)  → "$2a$10$N9qo8uLO..."
 *   BCrypt.matches("myPassword123", encoded) → true
 *   BCrypt.matches("wrongPassword", encoded) → false
 *   Key: you can NEVER reverse the hash to get original password
 *
 * KEY ANNOTATIONS:
 *   @EnableWebSecurity       → activates Spring Security for MVC/Servlet
 *   @EnableMethodSecurity    → enables @PreAuthorize on methods
 *   @PreAuthorize("hasRole('ADMIN')") → only ADMIN can call this method
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/users/auth/**").permitAll()
                // Allow registration/login endpoints without authentication
                .requestMatchers("/api/v1/users").permitAll()
                .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
