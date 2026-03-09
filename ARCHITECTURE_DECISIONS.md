# Architecture Decision Records (ADR)

## Overview

This document explains the key architectural decisions made in this microservices project and the rationale behind them.

---

## ADR-001: Microservices Architecture

**Decision:** Use microservices instead of monolithic architecture

**Rationale:**
- **Scalability**: Scale individual services independently
- **Resilience**: Failure in one service doesn't crash entire system
- **Technology Freedom**: Different services can use different tech stacks
- **Team Autonomy**: Different teams can own different services
- **Deployment**: Deploy services independently

**Tradeoffs:**
- **Complexity**: Distributed systems are harder to reason about
- **Network Latency**: Inter-service calls are slower than in-process calls
- **Data Consistency**: Distributed transactions are challenging
- **Operational Overhead**: More services to monitor and manage

**Mitigation:**
- Use API Gateway for unified entry point
- Implement circuit breakers for resilience
- Use async messaging for loose coupling
- Comprehensive monitoring with Zipkin

---

## ADR-002: Spring Boot Framework

**Decision:** Use Spring Boot for all microservices

**Rationale:**
- **Maturity**: 10+ years of development, battle-tested
- **Ecosystem**: Extensive library support (Cloud, Data, Security, etc.)
- **Auto-Configuration**: Reduces boilerplate significantly
- **Community**: Large community with extensive documentation
- **Job Market**: High demand for Spring Boot developers

**Alternatives Considered:**
- Quarkus: Faster startup, but smaller ecosystem
- Vert.x: More async-native, but steeper learning curve
- Dropwizard: Lightweight, but less comprehensive

**Decision:** Use Spring Boot for all services due to balance of power and simplicity

---

## ADR-003: Service Registry with Eureka

**Decision:** Use Netflix Eureka for service discovery

**Rationale:**
- **Dynamic Registration**: Services register/deregister automatically
- **Load Balancing**: Built-in client-side load balancing
- **Health Checks**: Automatic health monitoring
- **Spring Integration**: Seamless integration with Spring Cloud

**Alternatives Considered:**
- Consul: More powerful, but more complex
- ZooKeeper: Requires manual configuration
- Kubernetes Service Discovery: Requires K8s

**Decision:** Eureka for simplicity and Spring integration. Can migrate to Kubernetes later.

---

## ADR-004: API Gateway Pattern

**Decision:** Implement API Gateway with Spring Cloud Gateway

**Rationale:**
- **Single Entry Point**: Clients access one URL, not multiple service URLs
- **Cross-Cutting Concerns**: Handle authentication, logging, rate limiting in one place
- **Backward Compatibility**: Can change internal service locations
- **Load Balancing**: Distribute requests across service instances
- **Protocol Translation**: Convert between protocols if needed

**Responsibilities:**
```
API Gateway
├── Authentication (JWT validation)
├── Routing (path-based to services)
├── Rate Limiting (protect backends)
├── Circuit Breaking (handle failures)
├── Logging (centralized request/response)
└── CORS (cross-origin requests)
```

**Alternatives:**
- Kong: More powerful, but more overhead
- AWS API Gateway: Cloud-specific
- Nginx: Reverse proxy, but no dynamic routing

---

## ADR-005: Configuration Management with Spring Cloud Config

**Decision:** Use Spring Cloud Config Server for centralized configuration

**Rationale:**
- **Centralized**: All configuration in one place
- **Environment-Specific**: Different configs per environment (dev/staging/prod)
- **Dynamic Refresh**: Change configuration without restarting services
- **Version Control**: Track configuration changes with Git
- **Encryption**: Support for encrypted properties

**Alternative Configuration Sources:**
```
1. Config Server (Our Choice)
   ✓ Centralized
   ✓ Version control
   ✓ Dynamic refresh
   ✗ Requires separate server

2. Environment Variables
   ✓ Simple
   ✓ Container-friendly
   ✗ Not centralized
   ✗ Limited structure

3. Kubernetes ConfigMaps
   ✓ Container-native
   ✓ Centralized
   ✗ Requires Kubernetes
   ✗ No version history

4. Vault
   ✓ Secure
   ✓ Audit trail
   ✗ Complex setup
   ✗ Overkill for learning
```

**Our Choice:** Config Server for learning. In production, migrate to Vault/AWS Secrets Manager.

---

## ADR-006: Database Architecture - Multiple Datasources

**Decision:** Use multiple databases for different purposes

**Primary DB (PostgreSQL):**
- User data (main entity)
- Transactional consistency required
- Strong ACID guarantees needed

**Secondary DB (MySQL):**
- Audit logs (append-only)
- Historical data
- Less consistency requirements
- Easier scaling for append operations

**Rationale:**
- **Separation of Concerns**: Main app DB separate from audit DB
- **Scalability**: Audit DB can scale independently
- **Performance**: Audit writes don't impact main queries
- **Backup**: Different backup strategies for each

**Alternative: Single Database**
- Simpler to manage
- Easier transactions
- But audit logs block main operations
- Harder to scale

**Our Choice:** Multiple databases to demonstrate real-world separation while maintaining consistency via async updates.

---

## ADR-007: Caching Strategy with Redis

**Decision:** Use Redis for distributed caching

**What Gets Cached:**
```
✓ User objects (frequently read, infrequently changed)
✓ Order summaries (read-heavy)
✗ Real-time data (stock prices, etc.)
✗ Session tokens (stored in JWT instead)
```

**Invalidation Strategy:**
```
@Cacheable    → Get from cache, if miss read from DB
@CachePut     → Update cache after write
@CacheEvict   → Clear from cache on delete
```

**Rationale:**
- **Distributed Cache**: Shared across service instances
- **In-Memory**: Fast access (microseconds vs milliseconds from DB)
- **Expiration**: TTL-based cleanup
- **Monitoring**: Built-in commands for debugging

**Alternative: In-Memory Cache (Ehcache)**
- Faster (no network overhead)
- But not distributed
- Each instance has separate cache

**Decision:** Redis for distributed caching in microservices.

---

## ADR-008: JWT Authentication

**Decision:** Use JWT (JSON Web Tokens) for stateless authentication

**Flow:**
```
1. User login with credentials
2. Server validates and creates JWT
3. Client stores JWT (localStorage, cookie, etc.)
4. Client sends JWT in Authorization header
5. Server validates JWT signature
6. If valid, process request; if invalid, reject
```

**Why JWT?**
- **Stateless**: No session storage needed
- **Scalable**: Any server can validate the token
- **Distributed**: Works across microservices
- **Mobile-Friendly**: Easy for mobile apps
- **Standard**: Widely supported

**Token Structure:**
```
Header.Payload.Signature

Header: {"alg":"HS512","typ":"JWT"}
Payload: {"sub":"user123","role":"USER","exp":1234567890}
Signature: HMACSHA512(header.payload, secret)
```

**Security:**
- Secret key (long, random)
- HS512 algorithm (secure)
- Short expiration (5 hours)
- HTTPS only (in production)

**Alternatives:**
- Session Cookies: Stateful, doesn't scale
- OAuth2: Overkill for single organization
- Basic Auth: Credentials sent every request (insecure)

---

## ADR-009: Inter-Service Communication

**Decision:** Use both synchronous and asynchronous communication

**Synchronous (REST with WebClient):**
```
OrderService → UserService
"Is this user valid?"
"Yes/No" (immediate response)

Use When:
✓ Need immediate response
✓ Request-response pattern
✓ Data validation
```

**Asynchronous (Kafka/RabbitMQ):**
```
UserService → (publishes event)
OrderService → (listens and reacts)
(happens later, no waiting)

Use When:
✓ Don't need immediate response
✓ One-way notifications
✓ Multiple consumers
✓ Fire-and-forget operations
```

**Decision Criteria:**
| Scenario | Solution |
|----------|----------|
| "Validate user exists?" | Synchronous (WebClient) |
| "Notify about new order" | Asynchronous (Kafka) |
| "Get user details?" | Synchronous (cache) |
| "Update audit log" | Asynchronous (@Async) |

---

## ADR-010: Message Brokers - Kafka vs RabbitMQ

**Kafka (for Events):**
- **Multiple Consumers**: Many services listen to same event
- **Event Stream**: Events are replayed from beginning
- **Topic Partitions**: Built-in parallelism
- **Retention**: Messages persisted for days/weeks
- **Use Case**: "User created" → multiple services react

**RabbitMQ (for Tasks):**
- **Point-to-Point**: Message sent to one consumer
- **Task Queue**: Message deleted after processing
- **Acknowledge**: Ensure processing before deletion
- **Routing**: Complex routing rules possible
- **Use Case**: "Send email" → one service handles

**Our Implementation:**
```
UserService publishes → Kafka (user-events)
  ↓
OrderService listens → "User updated" event
  ↓
OrderService publishes → RabbitMQ (notification.queue)
  ↓
NotificationService listens → "Send email"
```

**Why Both?**
- Show different patterns
- Some events need multiple consumers (Kafka)
- Some tasks need single handler (RabbitMQ)

---

## ADR-011: Resilience with Resilience4j

**Decision:** Use Resilience4j for circuit breakers, retries, timeouts

**Pattern: Circuit Breaker**
```
CLOSED (normal)      OPEN (failing)      HALF_OPEN (testing)
   ↓                    ↓                     ↓
Accept requests    Reject requests    Limited requests
Monitor failures   Immediate reject   Check recovery
                   (fail-fast)        Return to CLOSED/OPEN
```

**When OrderService calls UserService:**
```java
@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
public void validateUser(Long userId) {
    UserDTO user = webClient.get().uri(...).block();
}

// Called when circuit OPEN or service fails
public void fallback(Long userId, Exception ex) {
    log.warn("User service unavailable");
    // Graceful degradation
}
```

**Benefits:**
- **Fail-Fast**: Don't wait for timeout
- **Graceful Degradation**: Fallback instead of crash
- **Self-Healing**: Automatic recovery check
- **Observable**: Know when services are failing

**Alternative: Simple Try-Catch**
```java
try {
    validateUser(userId);
} catch (Exception e) {
    log.error("Failed", e);
}
```
**Problem**: Doesn't protect against cascading failures

---

## ADR-012: Reactive Programming with WebFlux

**Decision:** Add reactive endpoints alongside MVC endpoints

**Blocking (MVC):**
```
Request → Thread 1 (blocked waiting for DB)
Request → Thread 2 (blocked waiting for DB)
Request → Thread 3 (blocked waiting for DB)
(Need many threads, memory intensive)
```

**Non-Blocking (WebFlux):**
```
Request → Event Loop
         ↓
         Async DB call (thread released)
         ↓
         When response arrives, continue
(Few threads, memory efficient)
```

**Our Approach:**
- Main endpoints: MVC (easier to understand)
- Streaming endpoints: WebFlux (efficient for streams)

```java
// Blocking - for list operations
@GetMapping
public Page<UserDTO> getUsers(Pageable pageable) {
    return userService.getAll(pageable);
}

// Non-blocking - for streaming
@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<UserDTO> streamUsers() {
    return reactiveUserService.getAllAsStream();
}
```

**Rationale:**
- Learn both paradigms
- Use right tool for job
- WebFlux for truly reactive needs
- MVC for simple CRUD

---

## ADR-013: Async Processing with @Async

**Decision:** Use @Async for fire-and-forget operations

**Use Cases:**
```
✓ Audit logging (don't wait for database)
✓ Sending emails (don't wait for email server)
✓ Background jobs (cache warming, cleanup)
✗ User-facing operations (need immediate response)
✗ Critical operations (need transaction handling)
```

**Implementation:**
```java
@Service
public class UserService {
    
    @Async("taskExecutor")  // Run in thread pool
    public CompletableFuture<Void> createAuditAsync(Long userId) {
        auditRepository.save(...);
        return CompletableFuture.completedFuture(null);
    }
    
    // Called from createUser()
    createAuditAsync(user.getId());  // Returns immediately
}
```

**Thread Pool Configuration:**
```yaml
Core Pool Size: 5      (min threads, always available)
Max Pool Size: 10      (max threads when busy)
Queue Capacity: 100    (tasks waiting in queue)
Keep Alive: 60 seconds (how long idle thread lives)
```

---

## ADR-014: Distributed Tracing with Zipkin

**Decision:** Use Micrometer + Zipkin for distributed tracing

**Problem in Microservices:**
```
Client → Gateway → UserService → Database
         (1)      (2)           (3)
       
If slow, where's the bottleneck?
Need to track across all services!
```

**Solution: Distributed Tracing**
```
Each request has unique TraceID: abc123
├─ Span 1: Gateway (10ms)
├─ Span 2: UserService (50ms)
└─ Span 3: Database (40ms)

Zipkin shows: UserService + Database are slow!
```

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
        
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signal -> log.info("Correlation-ID: {}", correlationId));
    }
}
```

---

## ADR-015: Monitoring with Actuator & Prometheus

**Decision:** Use Spring Boot Actuator + Prometheus for monitoring

**Metrics Collected:**
```
✓ JVM Memory usage
✓ HTTP request latency
✓ Database connection pool
✓ Cache hit/miss rates
✓ Circuit breaker states
✓ Kafka lag
```

**Endpoints:**
```
/actuator/health       → App running?
/actuator/metrics      → What metrics available?
/actuator/prometheus   → Export for Prometheus
/actuator/caches       → Cache stats
/actuator/circuitbreakers → CB states
```

**Alternative: Custom Logging**
- More work to implement
- Less standardized
- Harder to correlate data

---

## ADR-016: Testing Approach

**Decision:** Include test structure (actual tests can be added)

**Test Pyramid:**
```
            △
           /|\
          / | \
         /  |  \  E2E Tests (API tests)
        /   |   \
       /----|----\
      /     |     \ Integration Tests
     /      |      \
    /-------|-------\
   /        |        \ Unit Tests
  /__________________\

More tests at bottom, fewer at top
(More unit tests, fewer E2E tests)
```

**Our Structure:**
```
✓ Unit Tests: Service logic
✓ Integration Tests: Repository layer
✓ E2E Tests: Full API flow
(Postman collection for manual E2E)
```

---

## ADR-017: Error Handling Strategy

**Decision:** Unified error response format across all services

**Error Response Format:**
```json
{
  "success": false,
  "message": "User not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2024-03-09T10:30:00"
}
```

**Benefits:**
- **Consistency**: Clients expect same format
- **Error Codes**: Programmatic error handling
- **Traceability**: Timestamp for debugging
- **Documentation**: Clear error messages

**Implementation:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }
}
```

---

## ADR-018: Logging Strategy

**Decision:** Structured logging with SLF4J and Logback

**Log Levels:**
```
ERROR  → System errors that need attention
WARN   → Potential problems (fallbacks, retries)
INFO   → Important events (user login, order created)
DEBUG  → Detailed information for debugging
TRACE  → Very detailed (not used normally)
```

**Example:**
```java
@Slf4j
@Service
public class OrderService {
    
    public void createOrder(OrderDTO dto) {
        log.info("Creating order for user: {}", dto.getUserId());  // INFO
        
        if (userService.isInactive(dto.getUserId())) {
            log.warn("User inactive, proceeding with caution");     // WARN
        }
        
        try {
            orderRepository.save(...);
        } catch (Exception e) {
            log.error("Failed to save order", e);                   // ERROR with stack trace
        }
    }
}
```

**Production Config:**
```yaml
logging:
  level:
    com.learning: INFO          # My code
    org.springframework: WARN    # Framework
    org.hibernate: WARN         # Database
  pattern:
    console: "%d %level %logger{36} - %msg"
    file: "%d{ISO8601} %level %thread %logger - %msg"
```

---

## Summary Table

| Decision | Choice | Why |
|----------|--------|-----|
| Architecture | Microservices | Scalability, resilience |
| Framework | Spring Boot | Mature, comprehensive, community |
| Service Discovery | Eureka | Simple, well-integrated |
| API Gateway | Spring Cloud Gateway | Dynamic routing, features |
| Configuration | Spring Cloud Config | Centralized, dynamic refresh |
| Databases | Multiple (PostgreSQL + MySQL) | Separation of concerns |
| Cache | Redis | Distributed, fast |
| Authentication | JWT | Stateless, scalable |
| Sync Comm. | REST + WebClient | Request-response pattern |
| Async Comm. | Kafka + RabbitMQ | Different patterns |
| Resilience | Resilience4j | Comprehensive, Spring integrated |
| Reactive | WebFlux | When needed, not everywhere |
| Async Tasks | @Async | Fire-and-forget operations |
| Tracing | Zipkin | Distributed, visualized |
| Monitoring | Actuator + Prometheus | Standard Spring approach |
| Error Handling | Global handler + DTOs | Consistent, centralized |
| Logging | SLF4J + Logback | Standard, structured |

---

**All decisions are pragmatic, focusing on learning while following industry best practices.**

