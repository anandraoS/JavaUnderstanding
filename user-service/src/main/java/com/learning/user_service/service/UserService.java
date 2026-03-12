package com.learning.user_service.service;

import com.learning.common_library.constants.AppConstants;
import com.learning.common_library.dto.UserDTO;
import com.learning.common_library.event.UserEvent;
import com.learning.common_library.exception.BusinessException;
import com.learning.common_library.exception.ResourceNotFoundException;
import com.learning.user_service.entity.User;
import com.learning.user_service.entity.UserAudit;
import com.learning.user_service.repository.primary.UserRepository;
import com.learning.user_service.repository.secondary.UserAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * User Service Implementation
 * Demonstrates: Service layer, Business logic, Transactions, Caching, Async processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserAuditRepository userAuditRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getUsername());

        // Validate uniqueness
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("Username already exists", "USER_EXISTS");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Email already exists", "EMAIL_EXISTS");
        }

        // Create user
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .role(userDTO.getRole() != null ? userDTO.getRole() : AppConstants.ROLE_USER)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        // Audit log (async to secondary DB)
        createAuditAsync(user.getId(), user.getUsername(), "CREATED", "User account created");

        // Publish event to Kafka
        publishUserEvent(user, AppConstants.EVENT_USER_CREATED);

        return mapToDTO(user);
    }

    @Cacheable(value = AppConstants.CACHE_USERS, key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getActiveUsers(Pageable pageable) {
        log.info("Fetching active users");
        return userRepository.findByActiveTrue(pageable).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsersByName(String name, Pageable pageable) {
        log.info("Searching users by name: {}", name);
        return userRepository.searchByName(name, pageable).map(this::mapToDTO);
    }

    @CachePut(value = AppConstants.CACHE_USERS, key = "#id")
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update fields
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new BusinessException("Email already exists", "EMAIL_EXISTS");
            }
            user.setEmail(userDTO.getEmail());
        }

        user = userRepository.save(user);

        // Audit
        createAuditAsync(user.getId(), user.getUsername(), "UPDATED", "User profile updated");

        // Publish event
        publishUserEvent(user, AppConstants.EVENT_USER_UPDATED);

        return mapToDTO(user);
    }

    @CacheEvict(value = AppConstants.CACHE_USERS, key = "#id")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);

        // Audit
        createAuditAsync(user.getId(), user.getUsername(), "DELETED", "User account deleted");

        // Publish event
        publishUserEvent(user, AppConstants.EVENT_USER_DELETED);
    }

    @Async("taskExecutor")
    @Transactional("secondaryTransactionManager")
    public CompletableFuture<Void> createAuditAsync(Long userId, String username, String action, String details) {
        log.info("Creating audit log in MySQL for user: {} - action: {}", username, action);

        UserAudit audit = UserAudit.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .details(details)
                .performedAt(LocalDateTime.now())
                .build();

        userAuditRepository.save(audit);
        log.info("Audit log saved to MySQL for user: {}", username);
        return CompletableFuture.completedFuture(null);
    }

    private void publishUserEvent(User user, String eventType) {
        UserEvent event = UserEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(AppConstants.TOPIC_USER_EVENTS, event);
        log.info("Published Kafka event: {} for user: {}", eventType, user.getUsername());
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

