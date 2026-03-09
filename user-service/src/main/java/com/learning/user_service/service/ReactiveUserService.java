package com.learning.user_service.service;

import com.learning.common_library.dto.UserDTO;
import com.learning.user_service.entity.User;
import com.learning.user_service.repository.primary.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Reactive User Service using Project Reactor
 * Demonstrates: Reactive programming, WebFlux, Mono, Flux, Backpressure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveUserService {

    private final UserRepository userRepository;

    public Mono<UserDTO> getUserByIdReactive(Long id) {
        log.info("Reactive: Fetching user by ID: {}", id);

        return Mono.fromCallable(() -> userRepository.findById(id))
                .map(optUser -> optUser.orElseThrow(() ->
                    new RuntimeException("User not found with id: " + id)))
                .map(this::mapToDTO)
                .doOnSuccess(user -> log.info("Successfully fetched user: {}", user.getUsername()))
                .doOnError(error -> log.error("Error fetching user: {}", error.getMessage()));
    }

    public Flux<UserDTO> getAllUsersReactive() {
        log.info("Reactive: Fetching all users");

        return Flux.fromIterable(userRepository.findAll())
                .map(this::mapToDTO)
                .delayElements(Duration.ofMillis(100)) // Simulate processing delay
                .doOnComplete(() -> log.info("Completed fetching all users"))
                .doOnError(error -> log.error("Error fetching users: {}", error.getMessage()));
    }

    public Flux<UserDTO> getActiveUsersStreamReactive() {
        log.info("Reactive: Streaming active users");

        return Flux.fromIterable(userRepository.findAll())
                .filter(User::getActive)
                .map(this::mapToDTO)
                .delayElements(Duration.ofMillis(200)) // Simulate backpressure handling
                .doOnNext(user -> log.info("Streaming user: {}", user.getUsername()));
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

