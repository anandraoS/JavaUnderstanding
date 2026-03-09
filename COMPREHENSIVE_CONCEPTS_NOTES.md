# Deep Dive: Comprehensive Spring Boot & Microservices Concepts

## Table of Contents
1. [Spring Framework Core Concepts](#spring-framework-core)
2. [Spring Boot](#spring-boot)
3. [Spring Data JPA](#spring-data-jpa)
4. [Spring MVC & REST](#spring-mvc--rest)
5. [Spring Security](#spring-security)
6. [Microservices Architecture](#microservices-architecture)
7. [Asynchronous Programming](#asynchronous-programming)
8. [Reactive Programming](#reactive-programming)
9. [Caching Strategies](#caching-strategies)
10. [Distributed Systems](#distributed-systems)
11. [Resilience & Fault Tolerance](#resilience--fault-tolerance)

---

## Spring Framework Core

### 1. Dependency Injection (DI) & Inversion of Control (IoC)

**What is IoC?**
- Principle: Classes don't create their dependencies; the framework does
- Control of object creation is "inverted" to a container
- Enables loose coupling and testability

**Example in Our Code:**
```java
// Without DI (Tightly Coupled)
public class OrderService {
    private UserService userService = new UserService();
}

// With DI (Loose Coupling) - What we do
@Service
public class OrderService {
    private final UserService userService;  // Injected by Spring
    
    public OrderService(UserService userService) {
        this.userService = userService;
    }
}
```

**DI Mechanisms:**
1. **Constructor Injection** (Preferred)
   - Most explicit
   - Enables immutability
   - Used in our code with `@RequiredArgsConstructor`

2. **Setter Injection**
   - More flexible
   - Dependencies are optional

3. **Field Injection**
   - `@Autowired` on field
   - Not recommended (harder to test)

### 2. Bean Lifecycle

**Stages:**
```
1. Instantiation → Spring creates bean instance
2. Dependency Injection → Spring injects dependencies
3. BeanNameAware → if implements BeanNameAware
4. BeanFactoryAware → if implements BeanFactoryAware
5. ApplicationContextAware → if implements ApplicationContextAware
6. @PostConstruct → init method called
7. Bean ready for use
8. @PreDestroy → destroy method called
9. Bean destroyed
```

**In our code:**
```java
@Component
public class DataInitializer {
    
    @PostConstruct
    public void init() {
        // Initialize data after bean creation
        log.info("Application initialized");
    }
    
    @PreDestroy
    public void cleanup() {
        // Clean up before bean destruction
        log.info("Application shutting down");
    }
}
```

### 3. Bean Scopes

| Scope | Description | Use Case |
|-------|-------------|----------|
| **Singleton** | One instance per container | Services, Repositories (DEFAULT) |
| **Prototype** | New instance every time | Stateful objects |
| **Request** | One per HTTP request | Web layer |
| **Session** | One per session | User session data |
| **Global** | Global session | Portlet context |

```java
@Component
@Scope("prototype")  // New instance each time
public class PrototypeBean {}

@Component
@Scope("singleton")  // Same instance always (default)
public class SingletonBean {}
```

### 4. ApplicationContext vs BeanFactory

| Feature | BeanFactory | ApplicationContext |
|---------|------------|-------------------|
| Bean management | ✓ | ✓ |
| Event publishing | ✗ | ✓ |
| Resource loading | ✗ | ✓ |
| Message resolution | ✗ | ✓ |
| AOP support | ✗ | ✓ |
| Performance | Better | Slightly slower |

---

## Spring Boot

### 1. Auto-Configuration

**What it does:**
- Automatically configures Spring application based on classpath
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`

**How it works:**
```java
// Step 1: Spring looks for @SpringBootApplication
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

// Step 2: Auto-configuration classes are discovered
// Step 3: Beans are created based on conditions
// Step 4: Application starts
```

**Common Auto-Configurations:**
- DataSource auto-configuration (if DB driver in classpath)
- Kafka auto-configuration (if spring-kafka in classpath)
- Redis auto-configuration (if spring-data-redis in classpath)

### 2. Configuration Properties

**Three ways to configure:**

```yaml
# application.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

```java
// Using @Value
@Component
public class MyComponent {
    @Value("${spring.datasource.url}")
    private String dbUrl;
}

// Using @ConfigurationProperties (PREFERRED)
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    private String url;
    private String username;
    private String password;
    
    // Getters and setters
}
```

### 3. Actuator for Monitoring

**Endpoints:**
- `/actuator/health` - Application health
- `/actuator/metrics` - All metrics available
- `/actuator/prometheus` - Prometheus format metrics
- `/actuator/caches` - Cache information
- `/actuator/circuitbreakers` - Circuit breaker states
- `/actuator/env` - Environment properties
- `/actuator/loggers` - Logger configuration

**Our Implementation:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,caches
  endpoint:
    health:
      show-details: always
```

---

## Spring Data JPA

### 1. Entity Mapping

**Basic Entity:**
```java
@Entity
@Table(name = "users")
@Data  // Lombok: generates getters, setters, equals, hashCode, toString
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    @CreationTimestamp  // Auto-set on creation
    private LocalDateTime createdAt;
    
    @UpdateTimestamp  // Auto-set on update
    private LocalDateTime updatedAt;
}
```

### 2. Repository Pattern

**JpaRepository provides:**
- CRUD operations
- Pagination and sorting
- Batch operations
- Bulk updates/deletes

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Derived query (Spring generates SQL)
    Optional<User> findByUsername(String username);
    Page<User> findByActiveTrue(Pageable pageable);
    
    // JPQL query
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") String role);
    
    // Native SQL query
    @Query(value = "SELECT * FROM users WHERE email LIKE %:email%", nativeQuery = true)
    List<User> searchByEmail(@Param("email") String email);
}
```

### 3. Relationships

**One-to-Many (Order has many OrderItems):**
```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}

@Entity
public class OrderItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
```

**Cascade Types:**
- `PERSIST` - Save parent, children saved
- `MERGE` - Update parent, children updated
- `REMOVE` - Delete parent, children deleted
- `REFRESH` - Reload parent, children reloaded
- `DETACH` - Detach parent, children detached
- `ALL` - All of above

### 4. Pagination & Sorting

```java
// Usage
Pageable pageable = PageRequest.of(
    0,                                    // Page number (0-indexed)
    10,                                   // Page size
    Sort.by("id").descending()           // Sort criteria
);

Page<User> users = userRepository.findAll(pageable);

// In Controller
@GetMapping
public Page<UserDTO> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return userRepository
        .findAll(PageRequest.of(page, size))
        .map(this::mapToDTO);
}
```

### 5. Multiple Datasources

**Primary Database (PostgreSQL):**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.learning.user_service.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {
    // Configuration
}
```

**Secondary Database (MySQL):**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.learning.user_service.repository.secondary",
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDataSourceConfig {
    // Configuration
}
```

---

## Spring MVC & REST

### 1. RESTful API Development

**REST Principles:**
- Client-Server architecture
- Statelessness (each request contains all needed info)
- Uniform interface (standard HTTP methods)
- Cacheable responses
- Layered system
- Code on demand (optional)

**HTTP Methods:**
- `GET` - Retrieve resource (safe, idempotent)
- `POST` - Create resource (not idempotent)
- `PUT` - Update entire resource (idempotent)
- `PATCH` - Partial update (not idempotent)
- `DELETE` - Delete resource (idempotent)

**Example:**
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.createUser(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 2. Request/Response Handling

**Content Negotiation:**
```java
@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, 
                        MediaType.APPLICATION_XML_VALUE})
public User getUser(@PathVariable Long id) {
    return userService.getUserById(id);
}
```

**Exception Handling:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest()
            .body(ApiResponse.<Map<String, String>>builder()
                .success(false)
                .errorCode("VALIDATION_ERROR")
                .data(errors)
                .build());
    }
}
```

### 3. Validation

**JSR-303 Validation:**
```java
@Data
public class UserDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    @Min(value = 0, message = "Age must be positive")
    private Integer age;
}

// Usage in Controller
@PostMapping
public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) {
    // If validation fails, GlobalExceptionHandler catches it
    return ResponseEntity.ok(userService.createUser(dto));
}
```

---

## Spring Security

### 1. Authentication & Authorization

**Authentication:** Verifying user identity (login)
**Authorization:** Checking permissions (what user can do)

**Security Filter Chain:**
```
HTTP Request
    ↓
SecurityFilterChain
    ├→ CsrfFilter
    ├→ AuthenticationFilter
    ├→ AuthorizationFilter
    └→ ...other filters
    ↓
Your Controller
    ↓
HTTP Response
```

### 2. JWT Implementation

**How JWT Works:**
1. User logs in with credentials
2. Server validates and creates JWT token
3. Client stores token (localStorage, cookies)
4. Client sends token in Authorization header
5. Server validates token signature
6. If valid, process request

**Token Structure:**
```
eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJqb2huZG9lIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcxMDAwMDAwMCwiZXhwIjoxNzEwMDE4MDAwfQ.
signature_here
```

**Our JWT Util:**
```java
@Component
public class JwtUtil {
    private static final String SECRET_KEY = "...";
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 hours
    
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public Boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
```

### 3. Method-Level Security

```java
@Service
public class UserService {
    
    // Only ADMIN role can delete
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Only authenticated users
    @PreAuthorize("isAuthenticated()")
    public UserDTO getUser(Long id) {
        return mapToDTO(userRepository.findById(id).orElseThrow());
    }
    
    // Complex expression
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#userId, principal)")
    public UserDTO updateUser(Long userId, UserDTO dto) {
        // Update logic
    }
}
```

### 4. Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/users/auth/**").permitAll()  // Public
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()                           // Protected
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
            )
            .httpBasic(basic -> basic.disable())  // No HTTP Basic
            .formLogin(form -> form.disable());   // No form login
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Microservices Architecture

### 1. Service Discovery (Eureka)

**Problem:** How do services find each other in distributed system?
**Solution:** Service Registry - central place where services register

**Registration Process:**
```
Service starts
    ↓
Sends heartbeat to Eureka: "I'm here at localhost:8081"
    ↓
Eureka registers service
    ↓
Other services query Eureka: "Where is user-service?"
    ↓
Eureka returns: "localhost:8081"
```

**Configuration:**
```yaml
# In service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true      # Register this service
    fetch-registry: true            # Download registry
  instance:
    lease-renewal-interval-in-seconds: 10

# In Eureka Server
eureka:
  client:
    register-with-eureka: false     # Don't register itself
    fetch-registry: false
  server:
    enable-self-preservation: false # Disable in development
```

### 2. API Gateway Pattern

**Problem:** Clients need to know all service addresses
**Solution:** Single entry point (API Gateway) routes to services

**Gateway Benefits:**
- Single entry point
- Cross-cutting concerns (auth, logging)
- Load balancing
- Rate limiting
- API versioning
- Protocol translation

**Our Gateway Routes:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service  # Load balanced
          predicates:
            - Path=/api/v1/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
```

### 3. Inter-Service Communication

**Synchronous (REST/WebClient):**
```java
// OrderService calls UserService
@Service
public class OrderService {
    
    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    public void validateUser(Long userId) {
        UserDTO user = webClientBuilder.build()
            .get()
            .uri("http://user-service/api/v1/users/" + userId)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .block();
    }
    
    public void validateUserFallback(Long userId, Exception ex) {
        log.warn("User service down, using fallback");
        // Fallback logic
    }
}
```

**Asynchronous (Kafka/RabbitMQ):**
```java
// User Service publishes event
@Service
public class UserService {
    public void createUser(UserDTO dto) {
        User user = userRepository.save(...);
        
        // Publish event
        UserEvent event = UserEvent.builder()
            .userId(user.getId())
            .eventType("USER_CREATED")
            .build();
        kafkaTemplate.send("user-events", event);
    }
}

// Order Service consumes event
@Component
public class UserEventListener {
    
    @KafkaListener(topics = "user-events", groupId = "order-service")
    public void handleUserEvent(UserEvent event) {
        if ("USER_CREATED".equals(event.getEventType())) {
            // React to user creation
        }
    }
}
```

---

## Asynchronous Programming

### 1. @Async Annotation

**Use Case:** Long-running tasks that don't need immediate response

```java
@Service
public class UserService {
    
    // Async method returns Future or Mono
    @Async("taskExecutor")
    public CompletableFuture<Void> createAuditAsync(Long userId) {
        Thread.sleep(2000);  // Simulates long operation
        auditRepository.save(...);
        return CompletableFuture.completedFuture(null);
    }
}

// Usage
userService.createAuditAsync(userId);  // Returns immediately, runs in thread pool
```

**Thread Pool Configuration:**
```java
@Configuration
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // Min threads
        executor.setMaxPoolSize(10);          // Max threads
        executor.setQueueCapacity(100);       // Queue size
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
```

### 2. Message Brokers

**Kafka (Event Streaming):**
- Multiple consumers can receive same message
- Message persistence (by default)
- Ordered processing per partition
- Used for events that multiple services care about

```java
// Producer
@Service
public class OrderService {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public void createOrder(OrderDTO dto) {
        Order order = orderRepository.save(...);
        
        OrderEvent event = OrderEvent.builder()
            .orderId(order.getId())
            .eventType("ORDER_CREATED")
            .build();
        
        kafkaTemplate.send("order-events", event);
    }
}

// Consumer
@Component
public class OrderEventListener {
    
    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderEvent(OrderEvent event) {
        if ("ORDER_CREATED".equals(event.getEventType())) {
            sendNotification(event);
        }
    }
}
```

**RabbitMQ (Task Queue):**
- Point-to-point communication
- Task distribution
- Priority queues possible
- Used for direct task delegation

```java
// Producer
@Service
public class OrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void createOrder(OrderDTO dto) {
        rabbitTemplate.convertAndSend(
            "notification.exchange",
            "notification.routing.key",
            new OrderNotification(...)
        );
    }
}

// Consumer
@Component
public class NotificationListener {
    
    @RabbitListener(queues = "notification.queue")
    public void handleNotification(OrderNotification notification) {
        sendEmail(notification);
    }
}
```

---

## Reactive Programming

### 1. Project Reactor - Mono & Flux

**Mono (0 or 1 element):**
```java
@Service
public class ReactiveUserService {
    
    public Mono<UserDTO> getUserByIdReactive(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id))
                .map(optUser -> optUser.orElseThrow())
                .map(this::mapToDTO)
                .doOnSuccess(user -> log.info("Got user: {}", user.getUsername()))
                .doOnError(error -> log.error("Error: {}", error.getMessage()));
    }
}

// Usage
Mono<UserDTO> userMono = reactiveUserService.getUserByIdReactive(1L);
userMono.subscribe(user -> System.out.println(user));
```

**Flux (0 or more elements):**
```java
public Flux<UserDTO> getAllUsersReactive() {
    return Flux.fromIterable(userRepository.findAll())
            .map(this::mapToDTO)
            .delayElements(Duration.ofMillis(100))  // Simulates backpressure
            .doOnNext(user -> log.info("Streaming: {}", user.getUsername()));
}

// Usage - Streaming response
@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<UserDTO> streamUsers() {
    return getAllUsersReactive();
}
```

### 2. WebFlux

**Key Difference from MVC:**
- MVC: Blocking I/O, one thread per request
- WebFlux: Non-blocking I/O, event-driven, uses fewer threads

**WebFlux Controller:**
```java
@RestController
@RequestMapping("/api/reactive")
public class ReactiveController {
    
    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserDTO>> getUser(@PathVariable Long id) {
        return reactiveUserService.getUserByIdReactive(id)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }
    
    @GetMapping(value = "/users/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserDTO> streamUsers() {
        return reactiveUserService.getAllUsersReactive();
    }
}
```

---

## Caching Strategies

### 1. Spring Cache Abstraction

**Annotations:**
```java
@Service
public class UserService {
    
    // Get from cache, if miss execute method
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        return mapToDTO(userRepository.findById(id).orElseThrow());
    }
    
    // Execute method and update cache
    @CachePut(value = "users", key = "#id")
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow();
        // Update logic
        return mapToDTO(userRepository.save(user));
    }
    
    // Remove from cache
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

### 2. Redis Configuration

**Cache TTL:**
```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes in ms
```

**Custom Configuration:**
```java
@Configuration
public class RedisConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}
```

---

## Distributed Systems

### 1. Distributed Tracing (Zipkin)

**Problem:** How to track request across services?
**Solution:** Correlation ID + Distributed tracing

**Implementation:**
```java
@Component
public class LoggingFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = UUID.randomUUID().toString();
        
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Correlation-ID", correlationId)
                .build();
        
        log.info("Request started - Correlation-ID: {}", correlationId);
        
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signal -> log.info("Request completed - Correlation-ID: {}", correlationId));
    }
}
```

**Zipkin Tracing:**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # Trace 100% of requests
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

---

## Resilience & Fault Tolerance

### 1. Circuit Breaker (Resilience4j)

**States:**
- **CLOSED:** Normal operation, requests pass through
- **OPEN:** Service failing, requests rejected immediately
- **HALF_OPEN:** Testing if service recovered, limited requests allowed

**Workflow:**
```
Normal requests (CLOSED)
    ↓
Failures exceed threshold
    ↓
Circuit opens (OPEN) - requests rejected with fallback
    ↓
After wait duration
    ↓
Try limited requests (HALF_OPEN)
    ↓
Success: Circuit closes (CLOSED)
Failure: Back to OPEN
```

**Configuration:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        sliding-window-size: 10            # Last 10 calls
        failure-rate-threshold: 50         # Fail if 50% fail
        wait-duration-in-open-state: 10s   # Wait before retrying
        minimum-number-of-calls: 5
```

**Usage:**
```java
@Service
public class OrderService {
    
    @CircuitBreaker(
        name = "userService",
        fallbackMethod = "validateUserFallback"
    )
    public void validateUser(Long userId) {
        // Call user service
        UserDTO user = webClientBuilder.build()
            .get()
            .uri("http://user-service/api/v1/users/" + userId)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .block();
    }
    
    // Called when circuit is OPEN or service fails
    public void validateUserFallback(Long userId, Exception ex) {
        log.warn("User service unavailable, using fallback");
        // Graceful degradation
    }
}
```

### 2. Retry Mechanism

```yaml
resilience4j:
  retry:
    instances:
      userService:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        # Retries: 1s, 2s, 4s
```

```java
@Retry(name = "userService", fallbackMethod = "fallback")
public void callUserService(Long userId) {
    // Will retry up to 3 times with backoff
}
```

### 3. Timeout

```yaml
resilience4j:
  timelimiter:
    instances:
      userService:
        timeout-duration: 3s
        cancel-running-future: true
```

---

## Scheduled Tasks

### 1. @Scheduled Annotation

```java
@Component
public class ScheduledTasks {
    
    // Run at fixed rate (every 5 minutes)
    @Scheduled(fixedRate = 300000)
    public void healthCheck() {
        log.info("Health check executed");
    }
    
    // Run at fixed delay (5 min after last execution ends)
    @Scheduled(fixedDelay = 300000)
    public void cleanupTask() {
        log.info("Cleanup executed");
    }
    
    // Run using cron expression (0 0 * * * *)
    @Scheduled(cron = "0 0 * * * *")  // Every hour at top
    public void hourlyReport() {
        log.info("Hourly report generated");
    }
    
    // Initial delay before first execution
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void delayedTask() {
        log.info("Delayed task");
    }
}
```

**Cron Syntax:**
```
┌───────────── second (0 - 59)
│ ┌───────────── minute (0 - 59)
│ │ ┌───────────── hour (0 - 23)
│ │ │ ┌───────────── day of month (1 - 31)
│ │ │ │ ┌───────────── month (1 - 12)
│ │ │ │ │ ┌───────────── day of week (0 - 6) (0 = Sunday)
│ │ │ │ │ │
0 0 * * * *  → Every day at midnight
0 */6 * * * * → Every 6 hours
0 9 * * MON-FRI → 9 AM on weekdays
```

---

## Summary Matrix

| Concept | When | Where | How |
|---------|------|-------|-----|
| DI | Always | Constructors | @RequiredArgsConstructor |
| Cache | Frequent reads | Service methods | @Cacheable |
| Async | Long tasks | Service methods | @Async |
| Circuit Breaker | External calls | Service methods | @CircuitBreaker |
| Kafka | Multiple consumers | Domain events | @KafkaListener |
| RabbitMQ | Task queue | Direct task | @RabbitListener |
| WebFlux | Reactive endpoints | Controllers | Mono/Flux |
| Scheduled | Periodic tasks | Any method | @Scheduled |
| Retry | Transient failures | External calls | @Retry |
| Timeout | Slow services | External calls | @TimeLimiter |

---

**This comprehensive guide covers all enterprise patterns used in our microservices project!**

