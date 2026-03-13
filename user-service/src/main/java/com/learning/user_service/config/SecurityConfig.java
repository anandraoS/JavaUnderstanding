package com.learning.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    /**
     * PSEUDOCODE — Why we permitAll() here:
     *   In a gateway-based architecture, JWT is validated at the GATEWAY level.
     *   The gateway adds X-Username and X-Role headers to downstream requests.
     *   The downstream services (user-service, order-service) TRUST these headers.
     *   Therefore, user-service does NOT re-validate JWT — the gateway already did it.
     *
     *   We still keep Spring Security for:
     *   1. CSRF protection (disabled for stateless API)
     *   2. @PreAuthorize on methods (e.g., deleteUser needs ADMIN role)
     *   3. PasswordEncoder bean for BCrypt hashing
     *
     *   If you wanted user-service to ALSO validate JWT independently,
     *   you would add a custom JwtAuthenticationFilter to the filter chain here.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints — always public (login, register)
                .requestMatchers("/api/v1/users/auth/**").permitAll()
                // Registration: POST /api/v1/users — explicitly permit POST
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                // GET all users (paginated) — public
                .requestMatchers(HttpMethod.GET, "/api/v1/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                // Reactive endpoints
                .requestMatchers("/api/v1/users/reactive/**").permitAll()
                // Actuator — public for health checks & monitoring
                .requestMatchers("/actuator/**").permitAll()
                // Swagger / OpenAPI — public
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // All other requests — permit (gateway already validated JWT)
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }
}
