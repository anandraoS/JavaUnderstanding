package com.learning.user_service.controller;

import com.learning.common_library.dto.UserDTO;
import com.learning.user_service.service.ReactiveUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive User Controller using WebFlux
 * Demonstrates: Reactive programming, Non-blocking I/O, Server-Sent Events
 */
@RestController
@RequestMapping("/api/v1/users/reactive")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reactive User APIs", description = "Reactive/Non-blocking user APIs")
public class ReactiveUserController {

    private final ReactiveUserService reactiveUserService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Reactive)")
    public Mono<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Reactive REST: Getting user by ID: {}", id);
        return reactiveUserService.getUserByIdReactive(id);
    }

    @GetMapping
    @Operation(summary = "Get all users (Reactive)")
    public Flux<UserDTO> getAllUsers() {
        log.info("Reactive REST: Getting all users");
        return reactiveUserService.getAllUsersReactive();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream active users (Server-Sent Events)")
    public Flux<UserDTO> streamActiveUsers() {
        log.info("Reactive REST: Streaming active users");
        return reactiveUserService.getActiveUsersStreamReactive();
    }
}

