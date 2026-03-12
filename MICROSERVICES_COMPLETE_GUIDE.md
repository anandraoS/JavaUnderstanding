# 🚀 Spring Boot Microservices — Complete Learning Guide (Basic → Advanced)

> **Version Matrix (MUST match across ALL services):**
> | Component | Version |
> |---|---|
> | **Spring Boot** | `3.3.5` |
> | **Spring Cloud** | `2023.0.3` |
> | **Java** | `17` |
> | **JJWT** | `0.12.3` |
> | **springdoc-openapi** | `2.6.0` |

---

## 📖 TABLE OF CONTENTS

1. [Architecture Overview](#1-architecture-overview)
2. [Version Compatibility (Why 4.0.3 Failed)](#2-version-compatibility)
3. [Service Registry (Eureka)](#3-service-registry-eureka)
4. [Config Server](#4-config-server)
5. [API Gateway](#5-api-gateway)
6. [User Service](#6-user-service)
7. [Order Service](#7-order-service)
8. [Common Library](#8-common-library)
9. [Database Configuration (JPA / H2 / PostgreSQL)](#9-database-configuration)
10. [Security & JWT](#10-security--jwt)
11. [Caching with Redis](#11-caching-with-redis)
12. [Kafka (Event-Driven Architecture)](#12-kafka-event-driven-architecture)
13. [RabbitMQ (Message Queues)](#13-rabbitmq-message-queues)
14. [Circuit Breaker (Resilience4j)](#14-circuit-breaker-resilience4j)
15. [Reactive Programming (WebFlux)](#15-reactive-programming-webflux)
16. [All Errors Fixed & Why](#16-all-errors-fixed--why)
17. [Startup Order on Mac](#17-startup-order-on-mac)
18. [Key Spring Boot Annotations Glossary](#18-key-annotations-glossary)

---

## 1. Architecture Overview

```
                    ┌─────────────────────┐
                    │   Config Server     │  (Port 8888)
                    │ Centralized Config  │  Stores YAML for all services
                    └─────────┬───────────┘
                              │ provides config
                              ▼
┌──────────┐    ┌─────────────────────┐    ┌──────────────┐
│  Client  │───▶│    API Gateway      │───▶│ User Service │ (Port 8081)
│ (Browser │    │   (Port 8080)       │    │  - REST API  │
│  Postman)│    │  - JWT Validation   │    │  - JPA + H2  │
└──────────┘    │  - Route Requests   │    │  - Security  │
                │  - Circuit Breaker  │    │  - Kafka     │
                │  - Load Balancing   │    │  - Cache     │
                └──────────┬──────────┘    └──────────────┘
                           │
                           │               ┌─────────────���─┐
                           └──────────────▶│ Order Service │ (Port 8082)
                                           │  - REST API   │
                                           │  - JPA + H2   │
                                           │  - Kafka      │
                                           │  - RabbitMQ   │
                                           │  - Resilience │
                                           └───────────────┘
                              │
                    ┌─────────┴───────────┐
                    │  Service Registry   │  (Port 8761)
                    │  (Eureka Server)    │  All services register here
                    └─────────────────────┘
```

### What Each Service Does:
| Service | Port | Role |
|---|---|---|
| **service-registry** | 8761 | Eureka Server — service discovery (who is where?) |
| **config-server** | 8888 | Centralized configuration (one place for all YAML) |
| **api-gateway** | 8080 | Single entry point — routes, JWT, circuit breaker |
| **user-service** | 8081 | CRUD users, authentication, JWT tokens |
| **order-service** | 8082 | CRUD orders, inter-service calls, messaging |
| **common-library** | N/A | Shared DTOs, utils, exceptions (JAR, not runnable) |

---

## 2. Version Compatibility

### ❌ What Was Wrong (Spring Boot 4.0.3)
```
Spring Boot 4.0.3 DOES NOT EXIST (as of 2026).
The latest stable is 3.x. Using a non-existent version means
Maven cannot download the parent POM → everything fails.
```

### ✅ Correct Compatibility Matrix
```
Spring Boot 3.3.x  ↔  Spring Cloud 2023.0.x  ↔  Java 17+
Spring Boot 3.2.x  ↔  Spring Cloud 2023.0.x  ↔  Java 17+
Spring Boot 3.1.x  ↔  Spring Cloud 2022.0.x  ↔  Java 17+
```

### Rule: EVERY microservice must use the SAME versions
```xml
<!-- In EVERY pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>                    <!-- SAME everywhere -->
</parent>
<properties>
    <java.version>17</java.version>             <!-- SAME everywhere -->
    <spring-cloud.version>2023.0.3</spring-cloud.version>  <!-- SAME everywhere -->
</properties>
```

---

## 3. Service Registry (Eureka)

### What Is It?
Think of it as a **phone directory** for microservices. Instead of hard-coding
`http://localhost:8081` for user-service, other services ask Eureka:
_"Where is user-service running?"_

### Key Concepts:
```
1. Eureka SERVER = the registry itself (service-registry project)
2. Eureka CLIENT = every other service that registers with it
3. Self-preservation = Eureka keeps entries even if heartbeats stop (prevents mass de-registration)
4. Heartbeat = each client sends "I'm alive" every 30 seconds
```

### application.yaml Explained:
```yaml
eureka:
  client:
    register-with-eureka: false   # Server doesn't register with itself
    fetch-registry: false          # Server doesn't need to fetch its own registry
  server:
    enable-self-preservation: false  # Disabled for dev (removes dead instances quickly)
```

### Annotation:
```java
@EnableEurekaServer   // Makes this app a Eureka Server
@EnableDiscoveryClient // Makes this app a Eureka Client (on all other services)
```

### How It Works:
```
1. service-registry starts on port 8761
2. user-service starts → registers itself as "user-service" at localhost:8081
3. order-service starts → registers itself as "order-service" at localhost:8082
4. api-gateway asks Eureka: "Where is user-service?" → gets localhost:8081
5. api-gateway routes request to user-service
```

---

## 4. Config Server

### What Is It?
Instead of putting database URLs, Kafka settings, etc. in each service's
application.yaml, you put them in ONE place (Config Server). Each service
asks Config Server for its configuration on startup.

### Key Concepts:
```
1. Config Server stores configs in: classpath:/config/ (native profile)
   OR a Git repository (production)
2. File naming: user-service.yaml, order-service.yaml, api-gateway.yaml
3. optional: prefix means "don't fail if config server is down"
```

### Client-side config:
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  cloud:
    config:
      fail-fast: false    # Don't crash if config server is down
```

### Why "optional:" Matters:
Without `optional:`, if Config Server is down, the service REFUSES TO START.
With `optional:`, it falls back to its own local application.yaml.

---

## 5. API Gateway

### What Is It?
A **single door** that all client requests go through. Instead of clients
knowing about user-service:8081, order-service:8082, they only know
gateway:8080.

### Key Concepts:
```
1. ROUTING: /api/v1/users/** → user-service
2. FILTERING: JWT validation, logging, rate limiting
3. LOAD BALANCING: lb://user-service (uses Eureka to find instances)
4. CIRCUIT BREAKER: if user-service is down, return fallback response
5. CORS: Cross-Origin Resource Sharing configuration
```

### Gateway is REACTIVE (WebFlux)
```
- Uses Netty, NOT Tomcat
- Uses ServerHttpRequest, NOT HttpServletRequest
- Uses Mono/Flux, NOT normal return types
- spring.main.web-application-type=reactive
```

### Route Configuration:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service          # lb:// = load-balanced via Eureka
          predicates:
            - Path=/api/v1/users/**       # Match this URL pattern
          filters:
            - name: CircuitBreaker        # Wrap with circuit breaker
              args:
                fallbackUri: forward:/fallback/user-service
```

### SecurityConfig (Reactive):
```java
@EnableWebFluxSecurity   // NOT @EnableWebSecurity (that's for MVC)
// Uses ServerHttpSecurity, NOT HttpSecurity
// Uses SecurityWebFilterChain, NOT SecurityFilterChain
```

---

## 6. User Service

### What It Demonstrates:
- REST API with Spring MVC
- Multiple datasources (Primary: users, Secondary: audit logs)
- Spring Security with JWT
- Caching with Redis (falls back to in-memory)
- Async processing with @Async
- Kafka event publishing
- Scheduled tasks

### Key Patterns:
```
Controller → Service → Repository → Database
     ↓           ↓
  Validation   Business Logic, Caching, Events
```

### Multiple Datasources:
```java
@Primary  // This datasource is the default
@ConfigurationProperties("spring.datasource.primary")  // Reads from YAML
@EnableJpaRepositories(
    basePackages = "com.learning.user_service.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
```

### Web + WebFlux Together:
```yaml
spring:
  main:
    web-application-type: servlet  # MUST set this when both are on classpath
```
Without this, Spring Boot sees both `spring-boot-starter-web` (MVC/Tomcat) 
and `spring-boot-starter-webflux` (Reactive/Netty) and doesn't know which to use.

---

## 7. Order Service

### What It Demonstrates:
- Circuit Breaker with Resilience4j
- Inter-service communication via WebClient
- Kafka consumer/producer
- RabbitMQ messaging (conditional — only when enabled)
- Async order processing

### Inter-Service Communication:
```java
// WebClient calls user-service through Eureka
@LoadBalanced  // Required for lb:// URLs
WebClient.Builder webClientBuilder() { return WebClient.builder(); }

// Usage:
webClientBuilder.build()
    .get()
    .uri("http://user-service/api/v1/users/" + userId)  // Eureka resolves this
    .retrieve()
    .bodyToMono(UserDTO.class)
    .block();
```

---

## 8. Common Library

### What Is It?
A shared JAR that contains code used by multiple services:
- **DTOs**: UserDTO, OrderDTO, ApiResponse (consistent API format)
- **Events**: UserEvent, OrderEvent (Kafka message format)
- **Exceptions**: ResourceNotFoundException, BusinessException
- **Utilities**: JwtUtil (token generation/validation)
- **Constants**: AppConstants (topics, cache names, roles)
- **Config**: OpenApiConfig (Swagger setup)

### Why `<optional>true</optional>` on Dependencies?
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <optional>true</optional>  <!-- Consumer must declare their own web/webflux -->
</dependency>
```
If NOT optional: api-gateway (WebFlux) would inherit MVC → crash!
Optional means: "I need this to compile, but consumers choose their own."

---

## 9. Database Configuration

### Local Development (H2 In-Memory):
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:userdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop   # Recreate tables on every restart
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true   # Access at http://localhost:8081/h2-console
```

### Production (PostgreSQL via Config Server):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update   # Only add new columns, don't drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### JPA Entity → Table Mapping:
```java
@Entity                          // This class maps to a database table
@Table(name = "users")           // Table name
public class User {
    @Id                          // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;
    
    @Column(unique = true, nullable = false)  // Constraints
    private String username;
    
    @CreationTimestamp            // Auto-set on insert
    private LocalDateTime createdAt;
}
```

---

## 10. Security & JWT

### How JWT Authentication Works:
```
1. Client POST /api/v1/users/auth/login {username, password}
2. AuthService validates credentials against database
3. JwtUtil generates token with username + role
4. Client receives: { token: "eyJhbGc...", type: "Bearer" }
5. Client sends token in header: Authorization: Bearer eyJhbGc...
6. API Gateway's JwtAuthenticationFilter validates token
7. Gateway adds X-Username, X-Role headers to forwarded request
8. Downstream service reads those headers
```

### JJWT 0.12.x API (NEW — old methods are deprecated):
```java
// OLD (deprecated):
Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
Jwts.builder().setClaims(claims).setSubject(subject).signWith(key, SignatureAlgorithm.HS512);

// NEW (0.12.x):
Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
Jwts.builder().claims(claims).subject(subject).signWith(key, Jwts.SIG.HS512);
```

### SecurityConfig (MVC vs WebFlux):
```java
// MVC (user-service):
@EnableWebSecurity
SecurityFilterChain securityFilterChain(HttpSecurity http) { ... }

// WebFlux (api-gateway):
@EnableWebFluxSecurity
SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) { ... }
```

---

## 11. Caching with Redis

### Concept:
```
Without cache:  Request → Service → Database (slow, every time)
With cache:     Request → Service → Cache HIT → Return (fast!)
                                  → Cache MISS → Database → Store in Cache → Return
```

### Annotations:
```java
@Cacheable(value = "users", key = "#id")   // Read from cache first
@CachePut(value = "users", key = "#id")    // Update cache after write
@CacheEvict(value = "users", key = "#id")  // Remove from cache on delete
```

### Fallback Pattern (when Redis isn't running):
```java
@ConditionalOnBean(RedisConnectionFactory.class)
public CacheManager redisCacheManager(...) { ... }  // Uses Redis

@ConditionalOnMissingBean(CacheManager.class)
public CacheManager fallbackCacheManager() {
    return new ConcurrentMapCacheManager("users", "orders");  // In-memory
}
```

---

## 12. Kafka (Event-Driven Architecture)

### Concept:
```
Instead of: Order-Service calls User-Service synchronously (coupled)
We do:      User-Service publishes "USER_CREATED" event to Kafka topic
            Order-Service consumes that event asynchronously (decoupled)
```

### Producer (user-service):
```java
kafkaTemplate.send("user-events", userEvent);  // Fire-and-forget
```

### Consumer (order-service):
```java
@KafkaListener(topics = "user-events", groupId = "order-service-group")
public void handleUserEvent(UserEvent event) { ... }
```

### Why `autoStartup = "false"` for Local Dev:
```java
@KafkaListener(topics = "user-events", autoStartup = "false")
// Without this, the listener tries to connect to Kafka broker on startup
// If no broker → timeout error → service fails to start
```

### Why `listener.auto-startup: false` in YAML:
```yaml
spring:
  kafka:
    listener:
      auto-startup: false  # Don't start Kafka consumer containers
```

---

## 13. RabbitMQ (Message Queues)

### Kafka vs RabbitMQ:
```
Kafka:    Log-based, high throughput, events stay (can replay)
RabbitMQ: Traditional queue, message consumed = gone, routing patterns
```

### Conditional Activation:
```java
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true")
public class RabbitMQConfig { ... }
// Only creates RabbitMQ beans when spring.rabbitmq.enabled=true
// This way the service starts without RabbitMQ locally
```

### Optional Bean Injection:
```java
@Autowired(required = false) RabbitTemplate rabbitTemplate;
// If rabbitTemplate is null → skip sending (log warning)
// If rabbitTemplate exists → send message normally
```

---

## 14. Circuit Breaker (Resilience4j)

### Concept:
```
Normal:  Order-Service → User-Service (works fine)
Problem: Order-Service → User-Service (DOWN! timeout 30s...)
         Order-Service → User-Service (DOWN! timeout 30s...)
         → Users waiting, threads exhausted, cascade failure!

With Circuit Breaker:
CLOSED:    Requests pass through normally
OPEN:      After 50% failure rate → stop calling, return fallback instantly
HALF-OPEN: After 10 seconds → try a few requests to see if service recovered
```

### Configuration:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        sliding-window-size: 10          # Look at last 10 calls
        failure-rate-threshold: 50       # Open if 50%+ fail
        wait-duration-in-open-state: 10s # Wait 10s before trying again
  retry:
    instances:
      userService:
        max-attempts: 3                  # Retry up to 3 times
        wait-duration: 1s               # Wait 1s between retries
```

### Annotation:
```java
@CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
@Retry(name = "userService")
public void validateUser(Long userId) { ... }

public void validateUserFallback(Long userId, Exception ex) {
    log.warn("User service unavailable, using fallback");
}
```

---

## 15. Reactive Programming (WebFlux)

### Concept:
```
Traditional (MVC):   1 thread per request (blocks while waiting for DB)
Reactive (WebFlux):  Non-blocking (thread freed while waiting, reused)
```

### Key Types:
```java
Mono<T>    // 0 or 1 element (like Optional, but async)
Flux<T>    // 0 to N elements (like Stream, but async)
```

### Example:
```java
public Mono<UserDTO> getUserByIdReactive(Long id) {
    return Mono.fromCallable(() -> userRepository.findById(id))  // Wrap blocking call
            .map(opt -> opt.orElseThrow(...))
            .map(this::mapToDTO);
}

public Flux<UserDTO> getAllUsersReactive() {
    return Flux.fromIterable(userRepository.findAll())           // Stream results
            .map(this::mapToDTO)
            .delayElements(Duration.ofMillis(100));              // Simulate backpressure
}
```

### Server-Sent Events (SSE):
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<UserDTO> streamActiveUsers() {
    return reactiveUserService.getActiveUsersStreamReactive();
}
// Client gets data pushed in real-time as a continuous stream
```

---

## 16. All Errors Fixed & Why

### Fix 1: Spring Boot 4.0.3 → 3.3.5
```
Problem: Spring Boot 4.x doesn't exist
Fix:     Changed ALL pom.xml to 3.3.5
         Changed spring-cloud.version to 2023.0.3
```

### Fix 2: Duplicate `spring:` Key in YAML
```yaml
# BROKEN (second spring: block overwrites first):
spring:
  application:
    name: user-service
spring:                    # ← DUPLICATE! YAML only keeps this one
  config:
    import: ...

# FIXED (single spring: block):
spring:
  application:
    name: user-service
  config:
    import: ...
```

### Fix 3: CachingConfigurerSupport Removed
```
Problem: extends CachingConfigurerSupport was removed in Spring Boot 3.x
Fix:     Changed to plain @Configuration class
```

### Fix 4: JJWT 0.12.x Deprecated Methods
```
Problem: parserBuilder(), setSigningKey(), SignatureAlgorithm.HS512 all deprecated
Fix:     parser().verifyWith(key), claims(), Jwts.SIG.HS512
```

### Fix 5: No External Infrastructure Required Locally
```
Problem: PostgreSQL, Redis, Kafka, RabbitMQ all needed to start
Fix:     - H2 in-memory databases (no PostgreSQL/MySQL needed)
         - Redis auto-config excluded, fallback ConcurrentMapCacheManager
         - Kafka listener.auto-startup=false, try-catch on publish
         - RabbitMQ @ConditionalOnProperty, @Autowired(required=false)
```

### Fix 6: MVC + WebFlux Clash
```
Problem: Both spring-boot-starter-web and webflux on classpath
Fix:     spring.main.web-application-type=servlet (forces Tomcat/MVC)
         api-gateway uses: web-application-type=reactive (forces Netty/WebFlux)
```

### Fix 7: Gateway Security
```
Problem: @EnableWebFluxSecurity with .anyExchange().authenticated()
         but no ReactiveUserDetailsService → blocks everything
Fix:     Added dummy MapReactiveUserDetailsService
         Changed to .anyExchange().permitAll() (JWT filter handles auth)
```

### Fix 8: springdoc-openapi Conflicts
```
Problem: common-library had springdoc-webmvc-ui → pulled into gateway (WebFlux)
Fix:     Removed from common-library, each service declares its own
         common-library only has swagger-models-jakarta (compile-only, optional)
```

### Fix 9: resilience4j-spring-boot3 Missing Version
```
Problem: order-service had resilience4j-spring-boot3 without version
Fix:     Removed redundant dependency (spring-cloud-starter-circuitbreaker-resilience4j
         already includes everything needed)
```

---

## 17. Startup Order on Mac

### Open 5 Terminal tabs and run in this order:

```bash
# Terminal 1: Service Registry (FIRST — everyone registers here)
cd service-registry
./mvnw spring-boot:run

# Terminal 2: Config Server (SECOND — provides config to others)
cd config-server
./mvnw spring-boot:run

# Terminal 3: API Gateway
cd api-gateway
./mvnw spring-boot:run

# Terminal 4: User Service
cd user-service
./mvnw spring-boot:run

# Terminal 5: Order Service
cd order-service
./mvnw spring-boot:run
```

### Before running, install common-library first:
```bash
cd common-library
./mvnw clean install -DskipTests
```

### Verify Everything is Running:
```
http://localhost:8761          → Eureka Dashboard (see registered services)
http://localhost:8081/actuator/health  → User Service health
http://localhost:8082/actuator/health  → Order Service health
http://localhost:8080/actuator/health  → API Gateway health
http://localhost:8081/h2-console       → H2 Database Console (user-service)
http://localhost:8081/swagger-ui.html  → Swagger API docs (user-service)
```

### Make sure you have:
```
✅ Java 17+ installed:     java -version
✅ Maven wrapper exists:   ls -la mvnw (should show the file)
✅ mvnw is executable:     chmod +x mvnw (run in each service directory)
```

---

## 18. Key Annotations Glossary

| Annotation | Where | What It Does |
|---|---|---|
| `@SpringBootApplication` | Main class | Combines @Configuration + @ComponentScan + @EnableAutoConfiguration |
| `@EnableEurekaServer` | service-registry | Makes this app a Eureka Server |
| `@EnableDiscoveryClient` | All clients | Registers with Eureka |
| `@EnableConfigServer` | config-server | Serves configuration to other services |
| `@EnableWebSecurity` | MVC services | Enables Spring Security (Servlet) |
| `@EnableWebFluxSecurity` | Gateway | Enables Spring Security (Reactive) |
| `@EnableCaching` | Services | Activates @Cacheable / @CacheEvict |
| `@EnableAsync` | Services | Activates @Async for background tasks |
| `@EnableScheduling` | user-service | Activates @Scheduled for cron jobs |
| `@RestController` | Controllers | @Controller + @ResponseBody |
| `@Service` | Service layer | Business logic component |
| `@Repository` | Data layer | Data access component |
| `@Configuration` | Config classes | Declares @Bean methods |
| `@Entity` | JPA entities | Maps class to database table |
| `@Transactional` | Service methods | Wraps in database transaction |
| `@Cacheable` | Service methods | Cache the result |
| `@CacheEvict` | Service methods | Remove from cache |
| `@Async` | Service methods | Run in background thread |
| `@Scheduled` | Methods | Run on schedule (cron) |
| `@CircuitBreaker` | Service methods | Wrap with circuit breaker |
| `@Retry` | Service methods | Auto-retry on failure |
| `@KafkaListener` | Listener methods | Consume Kafka messages |
| `@RabbitListener` | Listener methods | Consume RabbitMQ messages |
| `@ConditionalOnBean` | Bean methods | Only create if another bean exists |
| `@ConditionalOnProperty` | Config classes | Only activate if property is set |
| `@ConditionalOnMissingBean` | Bean methods | Fallback bean |
| `@LoadBalanced` | WebClient.Builder | Enable Eureka-based load balancing |
| `@Primary` | Bean methods | Preferred bean when multiple exist |
| `@Qualifier` | Injection points | Pick specific bean by name |
| `@Valid` | Controller params | Trigger validation |
| `@PreAuthorize` | Methods | Method-level security |

---

## Quick Reference: File → Concept Mapping

| File | Concept |
|---|---|
| `ServiceRegistryApplication.java` | Service Discovery |
| `ConfigServerApplication.java` | Centralized Configuration |
| `GatewayConfig.java` | API Gateway Routing |
| `JwtAuthenticationFilter.java` | Gateway-level JWT Validation |
| `SecurityConfig.java` (gateway) | WebFlux Security |
| `SecurityConfig.java` (user-service) | MVC Security |
| `PrimaryDataSourceConfig.java` | Multiple Datasources |
| `SecondaryDataSourceConfig.java` | Multiple Datasources |
| `RedisConfig.java` | Caching with Fallback |
| `KafkaConfig.java` | Event Topics |
| `RabbitMQConfig.java` | Message Queues (Conditional) |
| `Resilience4jConfig.java` | Circuit Breaker |
| `WebClientConfig.java` | Inter-service Communication |
| `AsyncConfig.java` | Thread Pool for @Async |
| `UserService.java` | Full CRUD + Cache + Events |
| `OrderService.java` | Circuit Breaker + WebClient |
| `ReactiveUserService.java` | Reactive Mono/Flux |
| `MessageProducerService.java` | RabbitMQ Producer (Optional) |
| `UserEventListener.java` | Kafka Consumer |
| `OrderMessageListener.java` | RabbitMQ Consumer (Conditional) |
| `ScheduledTasks.java` | Cron Jobs |
| `GlobalExceptionHandler.java` | Centralized Error Handling |
| `JwtUtil.java` | JWT Token Operations |

---

> **Remember**: On Mac, run `chmod +x mvnw` in each service directory before running `./mvnw spring-boot:run`

