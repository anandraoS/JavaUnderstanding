package com.learning.user_service.service;

import com.learning.common_library.dto.AuthRequest;
import com.learning.common_library.dto.AuthResponse;
import com.learning.common_library.util.JwtUtil;
import com.learning.user_service.entity.User;
import com.learning.user_service.repository.primary.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * Demonstrates: JWT authentication, Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.getActive()) {
            throw new BadCredentialsException("User account is inactive");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        log.info("User authenticated successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(5 * 60 * 60L) // 5 hours
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}

