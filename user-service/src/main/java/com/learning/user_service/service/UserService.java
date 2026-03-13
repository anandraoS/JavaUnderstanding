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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * ═══════════════════════════════════════════════════════════════════
 * USER SERVICE — Business Logic Layer
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Service layer, Business logic, Transactions, Caching,
 *               Async processing, Kafka events, Multi-DB audit
 *
 * CONCEPT — SERVICE LAYER PATTERN:
 *   Controller → receives HTTP request, validates input
 *   Service    → contains BUSINESS LOGIC (this class)
 *   Repository → talks to the database
 *
 *   Why separate layers?
 *   → Controller doesn't know about DB queries
 *   → Repository doesn't know about business rules
 *   → Service orchestrates everything
 *   → Easy to unit test each layer independently
 *
 * CONCEPT — @Transactional:
 *   Without: save user → save audit → audit FAILS → user is saved, audit is lost (INCONSISTENT!)
 *   With @Transactional: save user → save audit → audit FAILS → ROLLBACK user too (CONSISTENT!)
 *
 * CONCEPT — @Transactional(readOnly = true):
 *   Tells Hibernate: "this method only READS data, no writes"
 *   → Hibernate skips dirty-checking (faster)
 *   → Some DBs use read-only connections (can use replicas)
 */
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserAuditRepository userAuditRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * CONCEPT — @Autowired(required = false):
     *   KafkaTemplate may not be available if Kafka broker is unreachable.
     *   By making it optional, the service still starts without Kafka.
     *   All Kafka publish calls check for null before sending.
     */
    @Autowired(required = false)
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserService(UserRepository userRepository,
                       UserAuditRepository userAuditRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuditRepository = userAuditRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * PSEUDOCODE — User creation flow:
     *   1. Check username uniqueness → if exists → throw "USERNAME_EXISTS"
     *   2. Check email uniqueness → if exists → throw "EMAIL_EXISTS"
     *   3. BCrypt.encode(password) → hash the password
     *   4. Save user to PostgreSQL (primary DB)
     *   5. Async: save audit log to MySQL (secondary DB)
     *   6. Async: publish USER_CREATED event to Kafka
     *   7. Return UserDTO (without password hash)
     */
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

        // Create user entity from DTO
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

        // Audit log (async to secondary DB — won't block main thread)
        createAuditAsync(user.getId(), user.getUsername(), "CREATED", "User account created");

        // Publish event to Kafka (fire-and-forget — won't crash if Kafka is down)
        publishUserEvent(user, AppConstants.EVENT_USER_CREATED);

        return mapToDTO(user);
    }

    /**
     * PSEUDOCODE — How @Cacheable works:
     *   getUserById(42)
     *     → CacheManager.getCache("users").get("42")
     *     → Redis: GET "users::42"
     *     → MISS → execute method body → query DB → save to Redis → return
     *     → HIT → skip method body → return cached value immediately
     */
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

    /**
     * PSEUDOCODE — How @CachePut works:
     *   updateUser(42, dto)
     *     → execute method body → update DB → return result
     *     → CacheManager.getCache("users").put("42", result)
     *     → next getUserById(42) → gets the UPDATED value from cache
     *
     *   Difference from @Cacheable:
     *     @Cacheable: skip method if cache HIT
     *     @CachePut:  ALWAYS execute method, then UPDATE cache
     */
    @CachePut(value = AppConstants.CACHE_USERS, key = "#id")
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update only provided fields (partial update pattern)
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

        // Audit + Kafka event
        createAuditAsync(user.getId(), user.getUsername(), "UPDATED", "User profile updated");
        publishUserEvent(user, AppConstants.EVENT_USER_UPDATED);

        return mapToDTO(user);
    }

    /**
     * PSEUDOCODE — How @CacheEvict works:
     *   deleteUser(42)
     *     → execute method body → delete from DB
     *     → CacheManager.getCache("users").evict("42")
     *     → next getUserById(42) → cache MISS → goes to DB → user not found → 404
     */
    @CacheEvict(value = AppConstants.CACHE_USERS, key = "#id")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);

        createAuditAsync(user.getId(), user.getUsername(), "DELETED", "User account deleted");
        publishUserEvent(user, AppConstants.EVENT_USER_DELETED);
    }

    /**
     * PSEUDOCODE — Why @Async + try-catch:
     *   @Async("taskExecutor")  → runs on background thread "async-1"
     *
     *   Main thread: save user to PostgreSQL → respond to client (fast!)
     *   Background:  save audit to MySQL (slow, but doesn't block user)
     *
     *   If MySQL is DOWN → audit fails silently → user creation still succeeds
     *   In production, you'd use a retry queue or outbox pattern for reliability
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> createAuditAsync(Long userId, String username, String action, String details) {
        try {
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
        } catch (Exception e) {
            log.warn("Failed to save audit log to MySQL for user: {} — MySQL may be unavailable: {}",
                    username, e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * PSEUDOCODE — Why try-catch around Kafka publish:
     *   If Kafka is DOWN, we don't want user creation to FAIL.
     *   The user is already saved to PostgreSQL — that's the important part.
     *   The event is a "nice-to-have" notification for other services.
     *   In production, you'd use an OUTBOX PATTERN or retry queue.
     */
    private void publishUserEvent(User user, String eventType) {
        if (kafkaTemplate == null) {
            log.warn("KafkaTemplate not available — skipping event: {} for user: {}", eventType, user.getUsername());
            return;
        }
        try {
            UserEvent event = UserEvent.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .eventType(eventType)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(AppConstants.TOPIC_USER_EVENTS, event);
            log.info("Published Kafka event: {} for user: {}", eventType, user.getUsername());
        } catch (Exception e) {
            log.warn("Failed to publish Kafka event: {} for user: {} — Kafka may be unavailable: {}",
                    eventType, user.getUsername(), e.getMessage());
        }
    }

    /**
     * CONCEPT — Why mapToDTO?
     *   Entity has password hash → NEVER expose to client
     *   DTO has only safe fields → return to client
     *   Entity → DTO conversion happens in the service layer
     */
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

