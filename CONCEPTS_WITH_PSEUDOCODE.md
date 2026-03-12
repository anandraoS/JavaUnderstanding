# ═══════════════════════════════════════════════════════════════════════════
# SPRING BOOT MICROSERVICES — COMPLETE CONCEPT GUIDE WITH PSEUDOCODE
# ═══════════════════════════════════════════════════════════════════════════
# Every concept explained Basic → Advanced with pseudocode & file references
# All infrastructure: PostgreSQL, MySQL, Redis, Kafka, RabbitMQ, Zipkin
# ═══════════════════════════════════════════════════════════════════════════

## VERSIONS (Same across ALL 8 services)
- Spring Boot: 3.3.5
- Spring Cloud: 2023.0.3
- Java: 17
- JJWT: 0.12.3
- springdoc-openapi: 2.6.0


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 1: SERVICE REGISTRY (Eureka)
# ═══════════════════════════════════════════════════════════════════════════
# File: service-registry/ServiceRegistryApplication.java
# Port: 8761 | UI: http://localhost:8761

## WHAT IS IT?
A phone directory for microservices. Each service registers its address.
Other services look up addresses from Eureka instead of hardcoding IPs.

## WHY DO WE NEED IT?
WITHOUT Eureka:
  gateway config: user-service = http://192.168.1.5:8081
  Problem: IP changes? → gateway breaks. Scale to 3 instances? → can't.

WITH Eureka:
  gateway config: user-service = lb://user-service
  Eureka knows: user-service = [192.168.1.5:8081, 192.168.1.6:8081]
  Gateway asks Eureka → gets current address → routes there

## PSEUDOCODE
```
# Server side (service-registry)
@EnableEurekaServer                    # "I am the phone directory"
eureka.client.register-with-eureka: false  # Don't list yourself
eureka.client.fetch-registry: false        # You ARE the registry

# Client side (every other service)
@EnableDiscoveryClient                 # "Register me in the directory"
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/

# What happens on startup:
user-service starts → sends to Eureka:
  { name: "user-service", host: "192.168.1.5", port: 8081 }
Eureka stores this entry

# Every 30 seconds:
user-service sends heartbeat → "I'm still alive"
If no heartbeat for 90 seconds → Eureka removes entry (service is dead)
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 2: CONFIG SERVER (Centralized Configuration)
# ═══════════════════════════════════════════════════════════════════════════
# File: config-server/ConfigServerApplication.java
# Port: 8888

## WHAT IS IT?
Instead of each service having its own full application.yaml,
store ALL configs in ONE place. Services fetch their config on startup.

## WHY?
Change database password → update in ONE place → all services pick it up
No need to rebuild/redeploy every service

## PSEUDOCODE
```
# Server side
@EnableConfigServer
spring.cloud.config.server.native.search-locations: classpath:/config

# Config files stored as:
config/user-service.yaml    → served to user-service
config/order-service.yaml   → served to order-service
config/api-gateway.yaml     → served to api-gateway

# Client side
spring.config.import: optional:configserver:http://localhost:8888
# "optional:" = if config server is down, use my local yaml instead

# What happens on startup:
user-service starts → HTTP GET http://localhost:8888/user-service/default
Config server returns: { datasource.url: "jdbc:postgresql://...", ... }
user-service merges this with its local application.yaml
(config server values OVERRIDE local values)
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 3: API GATEWAY (Spring Cloud Gateway)
# ═══════════════════════════════════════════════════════════════════════════
# Files: api-gateway/config/GatewayConfig.java
#        api-gateway/filter/JwtAuthenticationFilter.java
#        api-gateway/filter/LoggingFilter.java
#        api-gateway/controller/FallbackController.java
# Port: 8080

## WHAT IS IT?
Single entry point for all microservices. Client only knows localhost:8080.

## PSEUDOCODE — Request flow (complete journey)
```
Client: POST http://localhost:8080/api/v1/orders
        Header: Authorization: Bearer eyJhbGci...
        Body: { userId: 1, items: [...] }

STEP 1: LoggingFilter (GlobalFilter, runs FIRST)
  → Generate correlation ID: "abc-123"
  → Log: "Incoming: POST /api/v1/orders | Correlation-ID: abc-123"
  → Add header: X-Correlation-ID: abc-123
  → Start timer

STEP 2: GatewayConfig route matching
  → Path "/api/v1/orders/**" matches route "order-service"
  → Apply filters: JwtAuthenticationFilter, CircuitBreaker

STEP 3: JwtAuthenticationFilter
  → Extract: "Bearer eyJhbGci..." → "eyJhbGci..."
  → JwtUtil.extractUsername(token) → "john_doe"
  → JwtUtil.validateToken(token, "john_doe") → true
  → Add headers: X-Username: john_doe, X-Role: ROLE_USER

STEP 4: CircuitBreaker
  → Is circuit OPEN? → NO → forward request
  → (If YES → redirect to /fallback/order-service → FallbackController)

STEP 5: Load Balancer
  → URI: "lb://order-service"
  → Ask Eureka: "Where is order-service?"
  → Eureka: "localhost:8082"
  → Forward to: http://localhost:8082/api/v1/orders

STEP 6: order-service processes request, returns response

STEP 7: LoggingFilter.doFinally()
  → Log: "Completed: POST /api/v1/orders | Status: 201 | Duration: 45ms"

STEP 8: Response sent to client
```

## KEY DIFFERENCE: REACTIVE vs SERVLET
```
api-gateway: web-application-type: REACTIVE (WebFlux, Netty server)
  → Uses: Mono<Void>, ServerWebExchange, ServerHttpRequest
  → @EnableWebFluxSecurity (reactive security)
  → WHY: Gateway handles thousands of connections, non-blocking is essential

user-service: web-application-type: SERVLET (MVC, Tomcat server)
  → Uses: ResponseEntity, HttpServletRequest
  → @EnableWebSecurity (servlet security)
  → WHY: Business logic is simpler with blocking MVC
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 4: SPRING DATA JPA + MULTIPLE DATABASES
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/config/PrimaryDataSourceConfig.java   (PostgreSQL)
#        user-service/config/SecondaryDataSourceConfig.java  (MySQL)
#        user-service/entity/User.java
#        user-service/entity/UserAudit.java
#        user-service/repository/primary/UserRepository.java
#        user-service/repository/secondary/UserAuditRepository.java

## PSEUDOCODE — How multiple datasources work
```
YAML:
  spring.datasource.primary.url:   jdbc:postgresql://localhost:5432/userdb
  spring.datasource.secondary.url: jdbc:mysql://localhost:3306/user_audit_db

PrimaryDataSourceConfig:
  @EnableJpaRepositories(basePackages = "...repository.primary")
  → UserRepository is in this package → uses PostgreSQL
  
SecondaryDataSourceConfig:
  @EnableJpaRepositories(basePackages = "...repository.secondary")
  → UserAuditRepository is in this package → uses MySQL

When UserService does:
  userRepository.save(user)        → INSERT INTO users ... (PostgreSQL)
  userAuditRepository.save(audit)  → INSERT INTO user_audit ... (MySQL)
```

## PSEUDOCODE — JPA Entity lifecycle
```
@Entity @Table(name = "users")
class User {
    @Id @GeneratedValue(IDENTITY)    // DB auto-generates ID
    Long id;
    
    @Column(unique = true)           // DB adds UNIQUE constraint
    String username;
    
    @CreationTimestamp               // Hibernate sets on INSERT
    LocalDateTime createdAt;
    
    @UpdateTimestamp                  // Hibernate updates on every UPDATE
    LocalDateTime updatedAt;
}

// What JPA generates:
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

## PSEUDOCODE — Spring Data JPA Repository (magic queries)
```
interface UserRepository extends JpaRepository<User, Long> {
    // Spring generates SQL automatically from method name!
    
    findByUsername(String username)
    → SELECT * FROM users WHERE username = ?
    
    findByEmail(String email)  
    → SELECT * FROM users WHERE email = ?
    
    existsByUsername(String username)
    → SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)
    
    findByFirstNameContainingOrLastNameContaining(String first, String last)
    → SELECT * FROM users WHERE first_name LIKE '%john%' OR last_name LIKE '%john%'
}
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 5: SPRING SECURITY + JWT AUTHENTICATION
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/config/SecurityConfig.java
#        user-service/service/AuthService.java
#        user-service/controller/AuthController.java
#        common-library/util/JwtUtil.java

## PSEUDOCODE — Complete auth flow
```
1. REGISTER: POST /api/v1/users/auth/register
   Body: { username: "john", password: "myPass123", email: "j@x.com" }
   → AuthService.register()
   → BCrypt.encode("myPass123") → "$2a$10$N9qo8..."
   → INSERT INTO users (username, password, ...) VALUES ('john', '$2a$10$N9qo8...', ...)
   → Response: { success: true, message: "User registered" }

2. LOGIN: POST /api/v1/users/auth/login
   Body: { username: "john", password: "myPass123" }
   → AuthService.authenticate()
   → SELECT * FROM users WHERE username = 'john'
   → BCrypt.matches("myPass123", "$2a$10$N9qo8...") → true
   → JwtUtil.generateToken("john", "ROLE_USER")
   → Response: { token: "eyJhbGciOiJIUzUxMi...", type: "Bearer", expiresIn: 18000 }

3. USE TOKEN: GET /api/v1/orders
   Header: Authorization: Bearer eyJhbGciOiJIUzUxMi...
   → Gateway's JwtAuthenticationFilter validates token
   → Adds X-Username: john, X-Role: ROLE_USER headers
   → Forwards to order-service

4. TOKEN STRUCTURE (JWT = three parts separated by dots):
   HEADER.PAYLOAD.SIGNATURE
   
   Header:  { "alg": "HS512", "typ": "JWT" }          → base64 encoded
   Payload: { "sub": "john", "role": "ROLE_USER",      → base64 encoded
              "iat": 1710000000, "exp": 1710018000 }
   Signature: HMACSHA512(header + "." + payload, SECRET_KEY)
   
   → Anyone can READ the payload (it's just base64)
   → But nobody can MODIFY it without knowing SECRET_KEY
   → Server verifies: does HMAC(header.payload) match the signature?
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 6: REDIS CACHING
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/config/RedisConfig.java
#        order-service/config/RedisConfig.java
# Port: 6379 | CLI: redis-cli

## PSEUDOCODE — How caching works
```
WITHOUT CACHE:
  getUserById(1) → SELECT * FROM users WHERE id = 1  (50ms, every time!)
  getUserById(1) → SELECT * FROM users WHERE id = 1  (50ms again)
  getUserById(1) → SELECT * FROM users WHERE id = 1  (50ms again)

WITH REDIS CACHE:
  getUserById(1) → Redis.GET("users::1") → MISS
                 → SELECT * FROM users WHERE id = 1  (50ms)
                 → Redis.SET("users::1", userJSON, TTL=10min)
                 → return user

  getUserById(1) → Redis.GET("users::1") → HIT! → return user (0.1ms!)
  getUserById(1) → Redis.GET("users::1") → HIT! → return user (0.1ms!)
  
  After 10 minutes → Redis auto-deletes entry → next call goes to DB again
```

## PSEUDOCODE — Cache annotations
```
@Cacheable(value = "users", key = "#id")      // READ: return cached if exists
public UserDTO getUserById(Long id) { ... }

@CachePut(value = "users", key = "#id")        // WRITE: update cache after save
public UserDTO updateUser(Long id, ...) { ... }

@CacheEvict(value = "users", key = "#id")      // DELETE: remove from cache
public void deleteUser(Long id) { ... }

// The "value" = cache name = like a HashMap name in Redis
// The "key" = the lookup key = the method parameter
// Together: Redis key = "users::42" → stores UserDTO as JSON
```

## VERIFY WITH REDIS CLI:
```bash
redis-cli
> KEYS *                    # See all cached keys
> GET "users::1"            # Get cached user with ID 1
> TTL "users::1"            # Seconds until expiry
> DEL "users::1"            # Manually delete
> FLUSHALL                  # Delete everything
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 7: APACHE KAFKA (Event-Driven Architecture)
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/config/KafkaConfig.java       (creates topic)
#        user-service/service/UserService.java       (publishes events)
#        order-service/config/KafkaConfig.java       (creates topic)
#        order-service/listener/UserEventListener.java (consumes events)
# Port: 9092

## CONCEPT — Kafka vs Direct HTTP calls
```
DIRECT CALL (Synchronous, coupled):
  order-service → HTTP GET http://user-service/users/1
  Problem: user-service is DOWN → order-service breaks too!
  Problem: user-service is SLOW → order-service is slow too!

KAFKA (Asynchronous, decoupled):
  user-service PUBLISHES event → Kafka stores it → order-service CONSUMES it
  user-service doesn't know/care if order-service exists
  order-service can consume events whenever it's ready (even after restart)
```

## PSEUDOCODE — Complete Kafka flow
```
1. KafkaConfig creates topic:
   TopicBuilder.name("user-events").partitions(3).replicas(1).build()
   → Kafka creates "user-events" topic with 3 partitions

2. User creates account:
   UserService.createUser() 
   → Save to PostgreSQL
   → Build event: { userId: 1, username: "john", eventType: "USER_CREATED" }
   → kafkaTemplate.send("user-events", event)
   → Kafka stores event in "user-events" topic, partition 0

3. Order-service consumes:
   @KafkaListener(topics = "user-events", groupId = "order-service-group")
   void handleUserEvent(UserEvent event) {
       // event.getEventType() = "USER_CREATED"
       // event.getUsername() = "john"
       log.info("New user registered: {}", event.getUsername());
   }

4. KEY CONCEPTS:
   Topic     = category of messages (like a folder)
   Partition = parallel processing unit within a topic
   Consumer Group = load-balance consumers across partitions
   
   3 partitions + 1 consumer → 1 consumer reads all 3
   3 partitions + 3 consumers → each reads 1 (max parallelism!)
   3 partitions + 5 consumers → 3 read 1 each, 2 are IDLE
```

## VERIFY WITH CLI:
```bash
kafka-topics --bootstrap-server localhost:9092 --list
kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events --from-beginning
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 8: RABBITMQ (Message Queuing)
# ═══════════════════════════════════════════════════════════════════════════
# Files: order-service/config/RabbitMQConfig.java
#        order-service/service/MessageProducerService.java
#        order-service/listener/OrderMessageListener.java
# Port: 5672 | UI: http://localhost:15672 (guest/guest)

## CONCEPT — Kafka vs RabbitMQ (when to use which)
```
KAFKA:  Event LOG → messages PERSIST → can REPLAY
        Best for: events, analytics, audit trails
        Example: "user was created" (you might need to replay this)

RABBITMQ: Message QUEUE → message CONSUMED → GONE
        Best for: task queues, notifications, commands
        Example: "send email to user" (once sent, done)
```

## PSEUDOCODE — RabbitMQ components
```
PRODUCER → EXCHANGE → ROUTING KEY → QUEUE → CONSUMER

1. Queue: a mailbox that stores messages
   Queue("order.queue", durable=true)  // survives broker restart

2. Exchange: a post office that routes messages
   DirectExchange("order.exchange")    // routes by exact routing key match

3. Binding: connects exchange to queue with a routing key
   bind(orderQueue).to(orderExchange).with("order.routing.key")
   → messages sent to order.exchange with key "order.routing.key"
     are routed to order.queue

4. Producer sends message:
   rabbitTemplate.convertAndSend(
       "notification.exchange",       // which post office
       "notification.routing.key",    // which mailbox
       { orderId: 1, status: "CREATED" }  // the letter
   );

5. Consumer receives:
   @RabbitListener(queues = "order.queue")
   void handleOrderMessage(Map<String, Object> message) {
       // message = { orderId: 1, status: "CREATED" }
       // Process it (send email, update inventory, etc.)
   }
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 9: RESILIENCE4J (Circuit Breaker + Retry)
# ═══════════════════════════════════════════════════════════════════════════
# Files: order-service/config/Resilience4jConfig.java
#        order-service/service/OrderService.java

## PSEUDOCODE — Circuit breaker states
```
CLOSED (normal operation):
  call 1: user-service responds 200 ✓
  call 2: user-service responds 200 ✓
  call 3: user-service responds 500 ✗ (failure #1)
  call 4: user-service responds 200 ✓
  call 5: user-service responds 500 ✗ (failure #2)
  Failure rate: 2/5 = 40% < 50% threshold → STAY CLOSED

  call 6: user-service responds 500 ✗ (failure #3)
  Failure rate: 3/5 = 60% > 50% threshold → SWITCH TO OPEN!

OPEN (circuit is broken):
  call 7: IMMEDIATELY returns fallback ("Service unavailable") — no HTTP call!
  call 8: IMMEDIATELY returns fallback — no HTTP call!
  ... wait 10 seconds (wait-duration-in-open-state) ...
  → SWITCH TO HALF_OPEN

HALF_OPEN (testing if service recovered):
  call 9: try user-service → responds 200 ✓ (1 of 3 permitted)
  call 10: try user-service → responds 200 ✓ (2 of 3)
  call 11: try user-service → responds 200 ✓ (3 of 3)
  All 3 succeeded → SWITCH BACK TO CLOSED!
  (if any failed → back to OPEN for another 10 seconds)
```

## PSEUDOCODE — Retry with exponential backoff
```
@Retry(name = "userService")
attempt 1: call user-service → TIMEOUT → wait 1 second
attempt 2: call user-service → TIMEOUT → wait 2 seconds (1 * multiplier 2)
attempt 3: call user-service → TIMEOUT → GIVE UP → throw exception
→ Circuit breaker counts this as a FAILURE
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 10: REACTIVE PROGRAMMING (WebFlux / Mono / Flux)
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/service/ReactiveUserService.java
#        user-service/controller/ReactiveUserController.java

## CONCEPT — Blocking vs Non-blocking
```
BLOCKING (Traditional MVC):
  Thread-1: query DB... [WAITING 50ms]... got result → process → respond
  Thread-2: query DB... [WAITING 50ms]... got result → process → respond
  100 requests = 100 threads = 100 × 1MB stack = 100MB RAM

NON-BLOCKING (Reactive):
  Thread-1: send DB query → FREED → handles other request
  Thread-1: DB result arrives → process → respond
  100 requests = 10 threads handle everything = 10MB RAM
```

## PSEUDOCODE — Mono vs Flux
```
Mono<T> = 0 or 1 result (like Optional, but async)
  Mono.fromCallable(() -> userRepository.findById(1))
      .map(user -> mapToDTO(user))          // transform
      .doOnSuccess(u -> log.info("Got it")) // side effect
      .doOnError(e -> log.error("Failed"))  // error handling

Flux<T> = 0 to N results (like Stream, but async)
  Flux.fromIterable(userRepository.findAll())
      .filter(User::getActive)              // filter active users
      .map(this::mapToDTO)                  // transform each
      .delayElements(Duration.ofMillis(100))// simulate backpressure

// Server-Sent Events (SSE) — real-time streaming to client
@GetMapping(value = "/stream", produces = TEXT_EVENT_STREAM_VALUE)
Flux<UserDTO> streamUsers() {
    // Client keeps connection open, receives users one by one
    // Browser: EventSource("http://localhost:8081/api/v1/users/reactive/stream")
}
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 11: ZIPKIN (Distributed Tracing)
# ═══════════════════════════════════════════════════════════════════════════
# Port: 9411 | UI: http://localhost:9411

## PSEUDOCODE — How tracing works
```
1. Client sends: GET /api/v1/orders/1

2. Gateway creates TRACE ID: "trace-abc-123"
   Gateway creates SPAN: { service: "gateway", duration: 2ms }
   → sends span to Zipkin

3. Gateway forwards to order-service with trace-abc-123 in headers

4. Order-service creates SPAN: { service: "order-service", duration: 15ms }
   Order-service calls user-service (WebClient)
   → sends span to Zipkin

5. User-service creates SPAN: { service: "user-service", duration: 8ms }
   User-service queries PostgreSQL
   → sends span to Zipkin

6. Zipkin UI shows:
   trace-abc-123
   ├── gateway           [2ms]  ──────────────────────────────────
   ├── order-service    [15ms]     ─────────────────────────
   ├── user-service      [8ms]          ───────────
   └── postgresql         [3ms]            ─────

YAML config in each service:
  management.tracing.sampling.probability: 1.0  # trace 100% of requests
  management.zipkin.tracing.endpoint: http://localhost:9411/api/v2/spans
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 12: ASYNC PROCESSING (@Async)
# ═══════════════════════════════════════════════════════════════════════════
# Files: user-service/config/AsyncConfig.java
#        order-service/config/AsyncConfig.java
#        user-service/service/UserService.java (createAuditAsync)

## PSEUDOCODE
```
WITHOUT @Async:
  createUser() {
      user = save to PostgreSQL        // 5ms  — MAIN THREAD
      audit = save to MySQL            // 20ms — MAIN THREAD WAITS
      kafkaTemplate.send(event)        // 15ms — MAIN THREAD WAITS
      return user;                     // Total: 40ms before client gets response
  }

WITH @Async:
  createUser() {
      user = save to PostgreSQL        // 5ms  — MAIN THREAD
      createAuditAsync(user)           // returns IMMEDIATELY (0ms)
      publishUserEvent(user)           // 15ms — but Kafka is fast
      return user;                     // Total: 20ms before client gets response
  }
  
  @Async("taskExecutor")              // runs on BACKGROUND THREAD
  createAuditAsync(user) {
      audit = save to MySQL            // 20ms — on "async-1" thread
  }

ThreadPool: corePoolSize=5, maxPoolSize=10, queueCapacity=100
  → 5 threads always ready
  → Up to 10 threads under load
  → Up to 100 tasks queued before rejection
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 13: SCHEDULED TASKS (@Scheduled)
# ═══════════════════════════════════════════════════════════════════════════
# File: user-service/scheduler/ScheduledTasks.java

## PSEUDOCODE — Cron expression format
```
 ┌─── second (0-59)
 │ ┌─── minute (0-59)
 │ │ ┌─── hour (0-23)
 │ │ │ ┌─── day of month (1-31)
 │ │ │ │ ┌─── month (1-12)
 │ │ │ │ │ ┌─── day of week (0-7, SUN=0=7)
 │ │ │ │ │ │
 0 */30 * * * *    → every 30 minutes (at second 0)
 0 0 * * * *       → top of every hour
 0 0 0 * * *       → midnight every day
 0 0 9 * * MON-FRI → 9 AM on weekdays

@Scheduled(fixedRate = 300000)  → every 5 minutes (milliseconds)
@Scheduled(fixedDelay = 60000)  → 1 minute AFTER last execution finished
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 14: ACTUATOR + MONITORING
# ═══════════════════════════════════════════════════════════════════════════

## ENDPOINTS
```
GET /actuator/health          → { status: "UP", db: "UP", redis: "UP" }
GET /actuator/info            → application info
GET /actuator/metrics         → list of all metrics
GET /actuator/prometheus      → metrics in Prometheus format
GET /actuator/circuitbreakers → circuit breaker states (CLOSED/OPEN)
GET /actuator/caches          → list of all caches

# Gateway-specific:
GET /actuator/gateway/routes  → list all configured routes
```


# ═══════════════════════════════════════════════════════════════════════════
# CONCEPT 15: COMMON LIBRARY (Shared Code)
# ═══════════════════════════════════════════════════════════════════════════

## WHY?
```
Without common library:
  user-service has UserDTO.java
  order-service has UserDTO.java (DUPLICATE!)
  Change UserDTO → must change in BOTH places

With common library:
  common-library has UserDTO.java (SINGLE source of truth)
  user-service depends on common-library → gets UserDTO
  order-service depends on common-library → gets same UserDTO
```

## WHY <optional>true</optional> ON DEPENDENCIES?
```
common-library pom.xml:
  spring-boot-starter-web: optional=true

Without optional:
  api-gateway depends on common-library
  → api-gateway inherits spring-boot-starter-web (MVC/Tomcat)
  → CONFLICT! api-gateway needs WebFlux/Netty, not MVC/Tomcat!

With optional:
  api-gateway depends on common-library
  → does NOT inherit spring-boot-starter-web
  → api-gateway uses its own spring-boot-starter-webflux
  → WORKS PERFECTLY!
```


# ═══════════════════════════════════════════════════════════════════════════
# FILE → CONCEPT MAP (Quick Reference)
# ═══════════════════════════════════════════════════════════════════════════

| File | Concepts |
|---|---|
| ServiceRegistryApplication.java | Eureka Server |
| ConfigServerApplication.java | Centralized Config |
| GatewayConfig.java | Routing, Load Balancing, Circuit Breaker |
| JwtAuthenticationFilter.java | Gateway JWT Validation, Request Mutation |
| LoggingFilter.java | Global Logging, Correlation ID |
| FallbackController.java | Circuit Breaker Fallback |
| SecurityConfig.java (gateway) | WebFlux Security, Reactive |
| SecurityConfig.java (user) | MVC Security, BCrypt, Stateless JWT |
| PrimaryDataSourceConfig.java | PostgreSQL, @Primary, HikariCP |
| SecondaryDataSourceConfig.java | MySQL, @Qualifier, Multi-DB |
| User.java | JPA Entity, Lombok, Timestamps |
| UserAudit.java | Secondary DB Entity |
| RedisConfig.java | Redis Caching, TTL, Serialization |
| KafkaConfig.java | Kafka Topics, Partitions |
| RabbitMQConfig.java | RabbitMQ Exchanges, Queues, Bindings |
| Resilience4jConfig.java | Circuit Breaker, Time Limiter |
| WebClientConfig.java | Inter-service HTTP, @LoadBalanced |
| AsyncConfig.java | Thread Pool, @Async |
| UserService.java | CRUD + Cache + Kafka + Async Audit |
| AuthService.java | JWT Login, BCrypt |
| OrderService.java | Circuit Breaker + WebClient + Kafka + RabbitMQ |
| MessageProducerService.java | RabbitMQ Producer |
| ReactiveUserService.java | Mono, Flux, Reactive Streams |
| UserEventListener.java | Kafka Consumer |
| OrderMessageListener.java | RabbitMQ Consumer |
| ScheduledTasks.java | @Scheduled, Cron Jobs |
| GlobalExceptionHandler.java | @ControllerAdvice, Error Handling |
| JwtUtil.java | JJWT 0.12.x API, Token Operations |


# ═══════════════════════════════════════════════════════════════════════════
# STARTUP COMMAND (Mac with Homebrew)
# ═══════════════════════════════════════════════════════════════════════════
```bash
# Start all infrastructure
brew services start postgresql && brew services start mysql
brew services start redis && brew services start zookeeper && brew services start kafka
brew services start rabbitmq
java -jar zipkin.jar &

# Create databases (first time only)
psql -U postgres -c "CREATE DATABASE userdb;"
psql -U postgres -c "CREATE DATABASE orderdb;"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS user_audit_db;"

# Build common library first
cd common-library && chmod +x mvnw && ./mvnw clean install -DskipTests

# Start microservices (each in separate terminal)
cd service-registry && ./mvnw spring-boot:run   # Port 8761
cd config-server && ./mvnw spring-boot:run      # Port 8888
cd user-service && ./mvnw spring-boot:run       # Port 8081
cd order-service && ./mvnw spring-boot:run      # Port 8082
cd api-gateway && ./mvnw spring-boot:run        # Port 8080
```

