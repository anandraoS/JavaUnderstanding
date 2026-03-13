# ═══════════════════════════════════════════════════════════════════════════════
# TECHNOLOGIES USED — Complete Reference Guide
# ═══════════════════════════════════════════════════════════════════════════════
# Every technology explained: What it is, Why we use it, How it works,
# Real-world use cases, and which microservice uses it.
# ═══════════════════════════════════════════════════════════════════════════════

## 📋 TECHNOLOGY STACK AT A GLANCE

| Technology | Version | Port | Purpose |
|---|---|---|---|
| Java | 17 | — | Programming language |
| Spring Boot | 3.3.5 | — | Application framework |
| Spring Cloud | 2023.0.3 | — | Microservice infrastructure |
| Netflix Eureka | (via Spring Cloud) | 8761 | Service Discovery |
| Spring Cloud Config | (via Spring Cloud) | 8888 | Centralized Configuration |
| Spring Cloud Gateway | (via Spring Cloud) | 8080 | API Gateway |
| Spring Security | (via Spring Boot) | — | Authentication & Authorization |
| Spring Data JPA | (via Spring Boot) | — | Database ORM |
| PostgreSQL | Latest | 5432 | Primary relational database |
| MySQL | Latest | 3306 | Secondary database (audit) |
| Redis | Latest | 6379 | Distributed caching |
| Apache Kafka | Latest | 9092 | Event streaming |
| RabbitMQ | Latest | 5672/15672 | Message queuing |
| Resilience4j | (via Spring Cloud) | — | Circuit breaker & retry |
| Zipkin | Latest | 9411 | Distributed tracing |
| Micrometer | (via Spring Boot) | — | Metrics & monitoring |
| Prometheus | (via Micrometer) | — | Metrics collection |
| SpringDoc OpenAPI | 2.5.0/2.6.0 | — | API documentation (Swagger) |
| JJWT | 0.12.3 | — | JWT token library |
| Lombok | (via Spring Boot) | — | Boilerplate reduction |
| HikariCP | (via Spring Boot) | — | Connection pooling |
| Project Reactor | (via WebFlux) | — | Reactive programming |
| Jackson | (via Spring Boot) | — | JSON serialization |


---

# ═══════════════════════════════════════════════════════════════════════════════
# 1. JAVA 17 (LTS)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Java is a statically-typed, object-oriented programming language that runs on the
JVM (Java Virtual Machine). Version 17 is a Long-Term Support (LTS) release.

## Why do we use it?
- **Platform independent**: "Write once, run anywhere" — compiles to bytecode
- **LTS support**: Security patches guaranteed until 2029
- **Rich ecosystem**: Millions of libraries, frameworks, and tools
- **Strong typing**: Catches errors at compile time, not runtime
- **Garbage collection**: Automatic memory management

## Key Java 17 features used:
```
Records              → compact data classes (though we use Lombok instead)
Sealed classes       → restrict class hierarchies
Text blocks          → multi-line strings with triple quotes
Pattern matching     → instanceof type checks with auto-casting
Switch expressions   → switch as an expression that returns values
```

## Where is it used?
Every single file in all 6 microservices.

## Real-world use cases:
- Enterprise applications (banking, healthcare, government)
- Android mobile apps
- Big data processing (Hadoop, Spark)
- Web servers and microservices


---

# ═══════════════════════════════════════════════════════════════════════════════
# 2. SPRING BOOT 3.3.5
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Spring Boot is a framework that makes it easy to create production-grade Spring
applications. It provides auto-configuration, embedded servers, and opinionated
defaults so you can focus on business logic.

## Why do we use it?
- **Auto-configuration**: automatically configures beans based on classpath
- **Embedded server**: Tomcat/Netty included — no external server needed
- **Starter dependencies**: `spring-boot-starter-web` brings everything for REST APIs
- **Production ready**: health checks, metrics, externalized config out of the box
- **Convention over configuration**: works with sensible defaults

## How it works:
```
WITHOUT Spring Boot:
  1. Download Tomcat separately
  2. Configure web.xml (100+ lines)
  3. Configure Spring context XML (200+ lines)
  4. Configure DataSource XML
  5. Package as WAR → deploy to Tomcat
  Total setup: 2-3 days

WITH Spring Boot:
  1. Add spring-boot-starter-web to pom.xml
  2. Write @SpringBootApplication class
  3. Run: ./mvnw spring-boot:run
  Total setup: 5 minutes
```

## Key annotations:
```
@SpringBootApplication   → combines @Configuration + @ComponentScan + @EnableAutoConfiguration
@RestController          → marks class as REST API controller
@Service                 → marks class as business logic layer
@Repository              → marks class as data access layer
@Configuration           → marks class as bean configuration
@Bean                    → method produces a Spring-managed object
@Autowired               → dependency injection (Spring provides the object)
@Value("${key}")         → inject value from application.yaml
```

## Where is it used?
Every microservice: user-service, order-service, api-gateway, config-server, service-registry, common-library.


---

# ═══════════════════════════════════════════════════════════════════════════════
# 3. SPRING CLOUD 2023.0.3
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Spring Cloud is a collection of tools for building distributed systems and
microservice architectures. It provides patterns like service discovery,
configuration management, circuit breakers, and API gateways.

## Why do we use it?
- **Service Discovery (Eureka)**: services find each other without hardcoded URLs
- **Config Server**: centralized configuration management
- **API Gateway**: single entry point with routing and filtering
- **Circuit Breaker**: fault tolerance with Resilience4j
- **Load Balancing**: automatic distribution across service instances

## How it works:
```
Spring Cloud is NOT a single library — it's an UMBRELLA project:

spring-cloud-starter-netflix-eureka-server   → Eureka Server
spring-cloud-starter-netflix-eureka-client   → Eureka Client
spring-cloud-starter-config                  → Config Client
spring-cloud-config-server                   → Config Server
spring-cloud-starter-gateway                 → API Gateway
spring-cloud-starter-circuitbreaker-resilience4j → Circuit Breaker
spring-cloud-starter-loadbalancer            → Client-side load balancing
```

## Where is it used?
All microservices use Spring Cloud components.


---

# ═══════════════════════════════════════════════════════════════════════════════
# 4. NETFLIX EUREKA (Service Discovery)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Eureka is a REST-based service registry where microservices register themselves
and discover other services. It was originally built by Netflix for their
microservice architecture.

## Why do we use it?
```
WITHOUT Eureka:
  order-service config: user-service-url = http://192.168.1.5:8081
  Problem 1: IP changes → must update config and redeploy
  Problem 2: user-service has 3 instances → can't load balance
  Problem 3: Instance goes down → still sending traffic to it

WITH Eureka:
  order-service config: user-service-url = http://user-service (logical name)
  Eureka knows: user-service = [192.168.1.5:8081, 192.168.1.6:8081, 192.168.1.7:8081]
  → Automatic load balancing across all instances
  → Instance goes down → Eureka removes it → no more traffic to dead instance
```

## How it works:
```
1. REGISTRATION:
   user-service starts → POST to Eureka: "I'm user-service at 192.168.1.5:8081"
   Eureka stores: { name: "user-service", instances: [{host: "192.168.1.5", port: 8081}] }

2. HEARTBEAT (every 30 seconds):
   user-service → Eureka: "I'm still alive"
   If no heartbeat for 90 seconds → Eureka removes instance

3. DISCOVERY:
   order-service needs user-service → GET from Eureka: "Where is user-service?"
   Eureka returns: [{host: "192.168.1.5", port: 8081}, {host: "192.168.1.6", port: 8081}]
   order-service picks one (round-robin) and sends request

4. SELF-PRESERVATION:
   If Eureka loses too many heartbeats at once (network issue, not service failure)
   → Eureka enters "self-preservation mode"
   → Keeps all registrations (doesn't remove services)
   → Prevents false positives during network partitions
```

## Real-world use cases:
- Netflix (thousands of microservices)
- Any cloud-native microservice architecture
- Dynamic scaling environments (Kubernetes, AWS ECS)

## Files:
- `service-registry/ServiceRegistryApplication.java` (Eureka Server)
- Every other service has `@EnableDiscoveryClient` (Eureka Client)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 5. SPRING CLOUD CONFIG (Centralized Configuration)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
A centralized configuration server that manages external properties for
applications across all environments (dev, staging, prod).

## Why do we use it?
```
WITHOUT Config Server:
  5 services × 3 environments = 15 config files to maintain
  Change DB password → update 5 files → rebuild → redeploy ALL 5 services

WITH Config Server:
  Change DB password → update 1 file on config server
  Services refresh config → no rebuild needed
  Version control: Git stores config history
```

## How it works:
```
Config Server (port 8888):
  - Stores YAML files in classpath:/config/
  - Naming: {application-name}.yaml or {application-name}-{profile}.yaml
  
Client (user-service):
  spring.config.import: optional:configserver:http://localhost:8888
  On startup → HTTP GET http://localhost:8888/user-service/default
  Config Server finds config/user-service.yaml → returns content
  user-service merges with local application.yaml
  Remote values OVERRIDE local values
  
  "optional:" prefix = if config server is down, use local config
```

## Real-world use cases:
- Multi-environment deployments (dev/staging/prod configs in one place)
- Secret management (database credentials, API keys)
- Feature flags (enable/disable features without redeployment)
- A/B testing configuration

## Files:
- `config-server/ConfigServerApplication.java`
- `config-server/src/main/resources/config/*.yaml`


---

# ═══════════════════════════════════════════════════════════════════════════════
# 6. SPRING CLOUD GATEWAY (API Gateway)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
An API Gateway built on Spring WebFlux (reactive/non-blocking). It's the single
entry point for all client requests, providing routing, filtering, load balancing,
and security.

## Why do we use it?
```
WITHOUT Gateway:
  Client must know every service URL:
  → http://192.168.1.5:8081/api/v1/users    (user-service)
  → http://192.168.1.6:8082/api/v1/orders   (order-service)
  Problem: CORS issues, no central auth, no rate limiting, service URLs exposed

WITH Gateway:
  Client only knows: http://localhost:8080
  → /api/v1/users/** → routed to user-service
  → /api/v1/orders/** → routed to order-service
  Benefits: central auth, rate limiting, CORS, circuit breaker, logging
```

## How it works:
```
Request → Gateway receives
  → LoggingFilter: log request, add correlation ID
  → Route matching: which path matches? /api/v1/users/** → user-service
  → JwtAuthenticationFilter: validate JWT, add X-Username header
  → CircuitBreaker: is service healthy? yes → forward; no → fallback
  → LoadBalancer: ask Eureka for service address
  → Forward to downstream service
  → Response flows back through filters
```

## Key concepts:
- **Route**: maps a URL path to a downstream service URI
- **Predicate**: condition to match (path, method, header, etc.)
- **Filter**: modify request/response (add headers, authenticate, circuit break)
- **lb://**: load-balanced URI resolved via Eureka

## Why Reactive (WebFlux)?
```
Gateway handles THOUSANDS of concurrent connections.
Blocking (Tomcat): 1 thread per connection = 200 threads = 200 connections max
Non-blocking (Netty): 10 threads handle thousands of connections
Gateway is I/O-intensive (mostly forwarding) → perfect for reactive
```

## Files:
- `api-gateway/config/GatewayConfig.java` (route definitions)
- `api-gateway/filter/JwtAuthenticationFilter.java` (JWT validation)
- `api-gateway/filter/LoggingFilter.java` (request/response logging)
- `api-gateway/controller/FallbackController.java` (circuit breaker fallback)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 7. SPRING SECURITY + JWT
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Spring Security is a powerful authentication and authorization framework.
JWT (JSON Web Token) is a standard for securely transmitting information as
a JSON object that is digitally signed.

## Why do we use it?
- **Authentication**: verify WHO the user is (login)
- **Authorization**: verify WHAT the user can do (roles: USER, ADMIN)
- **Stateless**: no server-side sessions needed (perfect for microservices)
- **CSRF protection**: prevent cross-site request forgery

## How JWT works:
```
JWT = three parts separated by dots: HEADER.PAYLOAD.SIGNATURE

HEADER:  { "alg": "HS512", "typ": "JWT" }       → algorithm used
PAYLOAD: { "sub": "john", "role": "ROLE_USER",   → user data (claims)
           "iat": 1710000000, "exp": 1710018000 }
SIGNATURE: HMACSHA512(base64(header) + "." + base64(payload), SECRET_KEY)

Security:
- Anyone can READ the payload (it's just base64-encoded, NOT encrypted)
- But nobody can MODIFY it without knowing the SECRET_KEY
- Server verifies: recalculate HMAC and compare with signature
- If someone changes the payload → HMAC won't match → REJECTED
```

## Authentication flow:
```
1. REGISTER: POST /api/v1/users { username, password, email }
   → BCrypt.encode(password) → save to DB

2. LOGIN: POST /api/v1/users/auth/login { username, password }
   → Find user in DB → BCrypt.matches(password, hash) → true
   → Generate JWT token → return to client

3. USE TOKEN: GET /api/v1/orders (Authorization: Bearer eyJhbGci...)
   → Gateway validates token → extracts username, role
   → Adds X-Username, X-Role headers → forwards to service
```

## BCrypt password hashing:
```
password = "myPassword123"
encoded = BCrypt.encode(password) → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
BCrypt.matches("myPassword123", encoded) → true
BCrypt.matches("wrongPassword", encoded) → false

Key properties:
- ONE-WAY: you can NEVER reverse the hash to get the original password
- SALTED: same password produces DIFFERENT hashes each time
- SLOW by design: makes brute-force attacks impractical
```

## Files:
- `user-service/config/SecurityConfig.java` (MVC security)
- `api-gateway/config/SecurityConfig.java` (WebFlux security)
- `common-library/util/JwtUtil.java` (JWT operations)
- `user-service/service/AuthService.java` (login logic)
- `user-service/controller/AuthController.java` (login endpoint)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 8. SPRING DATA JPA + HIBERNATE
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Spring Data JPA is an abstraction over JPA (Java Persistence API) that makes
database operations incredibly simple. Hibernate is the JPA implementation
that translates Java objects to SQL queries.

## Why do we use it?
```
WITHOUT JPA (raw JDBC):
  Connection conn = DriverManager.getConnection(url);
  PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
  ps.setLong(1, userId);
  ResultSet rs = ps.executeQuery();
  while (rs.next()) {
      user.setId(rs.getLong("id"));
      user.setUsername(rs.getString("username"));
      // ... 20 more lines of mapping
  }
  rs.close(); ps.close(); conn.close();   // Don't forget to close!

WITH JPA:
  Optional<User> user = userRepository.findById(userId);  // ONE LINE!
```

## How it works:
```
@Entity → tells JPA this class maps to a DB table
@Table(name = "users") → table name
@Id → primary key
@GeneratedValue(IDENTITY) → auto-increment
@Column(unique = true) → database constraint

JPA generates SQL automatically:
  userRepository.findByUsername("john")
  → SELECT * FROM users WHERE username = 'john'

  userRepository.save(user)
  → INSERT INTO users (username, email, ...) VALUES ('john', 'j@x.com', ...)

  userRepository.findByActiveTrue(pageable)
  → SELECT * FROM users WHERE active = true LIMIT 10 OFFSET 0
```

## Multiple Datasources:
```
This project demonstrates connecting to TWO databases simultaneously:
- PostgreSQL (primary): stores users and orders
- MySQL (secondary): stores audit logs

How Spring knows which DB to use:
  repository/primary/UserRepository.java    → uses PostgreSQL
  repository/secondary/UserAuditRepository.java → uses MySQL

Configured in:
  PrimaryDataSourceConfig.java   (@Primary, PostgreSQL)
  SecondaryDataSourceConfig.java (MySQL)
```

## Files:
- `user-service/entity/User.java`, `UserAudit.java`
- `order-service/entity/Order.java`, `OrderItem.java`
- `user-service/repository/primary/UserRepository.java`
- `user-service/repository/secondary/UserAuditRepository.java`
- `user-service/config/PrimaryDataSourceConfig.java`
- `user-service/config/SecondaryDataSourceConfig.java`


---

# ═══════════════════════════════════════════════════════════════════════════════
# 9. POSTGRESQL
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
PostgreSQL is an advanced, open-source relational database known for reliability,
data integrity, and extensive feature set.

## Why do we use it?
- **ACID compliance**: guarantees data consistency (Atomicity, Consistency, Isolation, Durability)
- **Advanced features**: JSON support, full-text search, window functions
- **Extensible**: custom data types, extensions (PostGIS for geospatial)
- **Concurrent access**: MVCC (Multi-Version Concurrency Control) — reads never block writes
- **Industry standard**: used by Apple, Instagram, Spotify, Reddit

## How it works in this project:
```
user-service → PostgreSQL database "userdb"
  Tables: users (id, username, email, password, first_name, last_name, role, active)

order-service → PostgreSQL database "orderdb"
  Tables: orders (id, order_number, user_id, total_amount, status, payment_method)
          order_items (id, order_id, product_name, quantity, price, subtotal)

Connection: jdbc:postgresql://localhost:5432/userdb
Driver: org.postgresql.Driver
Pool: HikariCP (default, fastest connection pool for Java)
```

## Setup (Mac):
```bash
brew install postgresql@15
brew services start postgresql
psql -U postgres -c "CREATE DATABASE userdb;"
psql -U postgres -c "CREATE DATABASE orderdb;"
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 10. MYSQL
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
MySQL is the world's most popular open-source relational database, known for
speed and ease of use.

## Why do we use it (in addition to PostgreSQL)?
This project uses MySQL as a SECONDARY database to demonstrate **multi-datasource
configuration** — a real enterprise pattern where different concerns use
different databases.

```
PostgreSQL: user data (must be CONSISTENT, ACID transactions)
MySQL: audit logs (write-heavy, can tolerate some loss)

This separation means:
- Audit writes don't slow down user queries
- MySQL can be on a different server for better performance
- If MySQL goes down, user operations still work
```

## Setup (Mac):
```bash
brew install mysql
brew services start mysql
mysql -u root -e "CREATE DATABASE IF NOT EXISTS user_audit_db;"
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 11. REDIS (Distributed Cache)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Redis is an in-memory data store used as a cache, message broker, and database.
It stores data in RAM, making it extremely fast (sub-millisecond latency).

## Why do we use it?
```
WITHOUT CACHE:
  getUserById(1) → SQL query to PostgreSQL → 50ms
  getUserById(1) → SQL query to PostgreSQL → 50ms (same query!)
  100 requests/sec = 100 DB queries/sec = DB under heavy load

WITH REDIS CACHE:
  getUserById(1) → check Redis → MISS → SQL query → 50ms → save to Redis
  getUserById(1) → check Redis → HIT → return instantly → 0.1ms!
  100 requests/sec = 1 DB query + 99 cache hits = DB barely touched
```

## How it works:
```
Data stored as key-value pairs in RAM:
  "users::42" → '{"id":42,"username":"john","email":"j@x.com"}'

Spring Cache annotations:
  @Cacheable  → check cache first, query DB only on miss
  @CachePut   → always execute method, then update cache
  @CacheEvict → remove entry from cache

TTL (Time-To-Live): entries auto-expire after 10 minutes
  → prevents stale data from accumulating
```

## Real-world use cases:
- Session storage (user sessions in web apps)
- Rate limiting (API request counting)
- Leaderboards (sorted sets)
- Real-time analytics (counters, HyperLogLog)
- Pub/Sub messaging

## Setup (Mac):
```bash
brew install redis
brew services start redis
redis-cli ping   # Should return PONG
```

## Files:
- `user-service/config/RedisConfig.java`
- `order-service/config/RedisConfig.java`


---

# ═══════════════════════════════════════════════════════════════════════════════
# 12. APACHE KAFKA (Event Streaming)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Apache Kafka is a distributed event streaming platform capable of handling
trillions of events per day. It's used for building real-time data pipelines
and streaming applications.

## Why do we use it?
```
PROBLEM: user-service creates a user → order-service needs to know about it

SOLUTION 1 (Direct HTTP call — BAD):
  user-service → HTTP POST to order-service: "User john was created"
  Problems:
  - user-service must KNOW about order-service (tight coupling)
  - order-service is DOWN? → user-service call fails
  - Add notification-service? → must update user-service to call it too

SOLUTION 2 (Kafka — GOOD):
  user-service → publish event to Kafka topic: "USER_CREATED"
  order-service → subscribes to topic → receives event when ready
  notification-service → also subscribes → gets same event
  Problems SOLVED:
  - user-service doesn't know/care about consumers (loose coupling)
  - order-service is DOWN? → events wait in Kafka → consumed when back up
  - Add new service? → just subscribe to topic → no changes to publisher
```

## How it works:
```
Key components:
  PRODUCER → sends messages to a TOPIC
  TOPIC → named category of messages (like a folder)
  PARTITION → parallel processing unit within a topic
  CONSUMER → reads messages from a topic
  CONSUMER GROUP → load-balances consumers across partitions
  BROKER → Kafka server that stores messages
  ZOOKEEPER → coordinates Kafka cluster (being replaced by KRaft)

Data flow:
  UserService.createUser()
    → kafkaTemplate.send("user-events", { userId: 1, type: "USER_CREATED" })
    → Kafka broker stores message in "user-events" topic, partition 0
    → UserEventListener.handleUserEvent() receives message

Message retention:
  Unlike RabbitMQ, Kafka KEEPS messages even after consumption
  Default: 7 days (configurable)
  Consumers can REPLAY from any offset (reprocess old events)
```

## Kafka vs RabbitMQ:
```
| Feature | Kafka | RabbitMQ |
|---|---|---|
| Model | Event log | Message queue |
| Persistence | Messages persist | Messages consumed = gone |
| Replay | Can replay from any point | Cannot replay |
| Throughput | Millions/sec | Thousands/sec |
| Use case | Events, analytics, audit | Tasks, notifications |
| Ordering | Per-partition ordering | Per-queue ordering |
```

## Setup (Mac):
```bash
brew install kafka    # includes zookeeper
brew services start zookeeper
brew services start kafka
kafka-topics --bootstrap-server localhost:9092 --list
```

## Files:
- `user-service/config/KafkaConfig.java` (topic creation)
- `user-service/service/UserService.java` (publishes USER events)
- `order-service/config/KafkaConfig.java` (topic creation)
- `order-service/listener/UserEventListener.java` (consumes USER events)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 13. RABBITMQ (Message Queuing)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
RabbitMQ is an open-source message broker that implements AMQP (Advanced Message
Queuing Protocol). It's designed for reliable message delivery between applications.

## Why do we use it?
```
Best for ONE-TIME TASKS that should be processed exactly once:
- Send confirmation email after order is placed
- Process payment
- Generate invoice PDF
- Update inventory count

Message consumed = message GONE (unlike Kafka which keeps messages)
```

## How it works:
```
Components:
  PRODUCER → sends message to EXCHANGE
  EXCHANGE → routes messages to QUEUES based on routing key
  QUEUE → stores messages until consumed
  CONSUMER → reads and processes messages

Exchange types:
  Direct   → exact routing key match (this project uses this)
  Topic    → pattern-based routing (order.* matches order.created)
  Fanout   → broadcast to ALL bound queues
  Headers  → route based on message headers

Flow in this project:
  1. Order created → MessageProducerService.sendOrderNotification()
  2. rabbitTemplate.convertAndSend("notification.exchange", "notification.routing.key", msg)
  3. RabbitMQ routes to notification.queue
  4. OrderMessageListener.handleOrderMessage() receives and processes
```

## Setup (Mac):
```bash
brew install rabbitmq
brew services start rabbitmq
# Management UI: http://localhost:15672 (guest/guest)
```

## Files:
- `order-service/config/RabbitMQConfig.java` (exchanges, queues, bindings)
- `order-service/service/MessageProducerService.java` (sends messages)
- `order-service/listener/OrderMessageListener.java` (receives messages)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 14. RESILIENCE4J (Circuit Breaker + Retry)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Resilience4j is a lightweight fault tolerance library designed for Java.
It provides circuit breaker, retry, rate limiter, bulkhead, and time limiter
patterns.

## Why do we use it?
```
PROBLEM (Cascading failure):
  order-service → calls user-service (DOWN!)
  → order-service waits 30 seconds for timeout
  → 100 concurrent requests = 100 threads blocked
  → order-service runs out of threads → ALSO GOES DOWN
  → api-gateway can't reach order-service → GATEWAY GOES DOWN
  → ENTIRE SYSTEM DOWN because ONE service failed!

SOLUTION (Circuit breaker):
  order-service → calls user-service (DOWN!)
  → Circuit breaker OPENS after 5 failures
  → Next 100 requests: INSTANT fallback response (< 1ms, no HTTP call)
  → order-service stays healthy
  → After 10 seconds: test if user-service is back
  → user-service recovered? → Circuit CLOSES → normal operation resumes
```

## How it works:
```
Three states:
  CLOSED  → all requests pass through → monitoring failure rate
  OPEN    → all requests get fallback → no actual calls made
  HALF_OPEN → limited test requests → decide to close or reopen

Configuration:
  slidingWindowSize: 10          → evaluate last 10 calls
  failureRateThreshold: 50      → 50% failures → OPEN circuit
  waitDurationInOpenState: 10s   → wait 10 seconds before testing
  permittedNumberOfCallsInHalfOpenState: 3 → test with 3 requests

Retry:
  maxAttempts: 3                 → try 3 times before giving up
  waitDuration: 1s               → wait 1 second between retries
  exponentialBackoff: 2x         → 1s, 2s, 4s between retries
```

## Files:
- `order-service/config/Resilience4jConfig.java`
- `order-service/service/OrderService.java` (@CircuitBreaker, @Retry)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 15. PROJECT REACTOR (WebFlux / Mono / Flux)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Project Reactor is a reactive programming library for building non-blocking
applications on the JVM. It implements the Reactive Streams specification.

## Why do we use it?
```
BLOCKING (Traditional MVC):
  Thread-1 handles request → calls DB → BLOCKS for 50ms → processes → responds
  Thread-2 handles request → calls DB → BLOCKS for 50ms → processes → responds
  Problem: 200 concurrent requests = 200 threads = high memory usage

NON-BLOCKING (Reactive):
  Thread-1 handles request → sends DB query → FREED → handles another request
  When DB responds → any available thread picks up and processes
  Result: 10 threads can handle thousands of concurrent requests
```

## Key types:
```
Mono<T>  → 0 or 1 result (like Optional, but asynchronous)
  Mono.fromCallable(() → userRepository.findById(1))
      .map(user → mapToDTO(user))
      .doOnSuccess(u → log.info("Got it"))
      .doOnError(e → log.error("Failed"))

Flux<T>  → 0 to N results (like Stream, but asynchronous)
  Flux.fromIterable(userRepository.findAll())
      .filter(User::getActive)
      .map(this::mapToDTO)
      .delayElements(Duration.ofMillis(100))

Server-Sent Events (SSE):
  @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
  Flux<UserDTO> streamUsers()
  → Client keeps connection open
  → Server pushes data as it becomes available
  → Perfect for real-time dashboards, notifications
```

## Files:
- `user-service/service/ReactiveUserService.java`
- `user-service/controller/ReactiveUserController.java`
- `api-gateway/` (entire gateway is reactive)


---

# ═══════════════════════════════════════════════════════════════════════════════
# 16. ZIPKIN (Distributed Tracing)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Zipkin is a distributed tracing system that helps gather timing data needed to
troubleshoot latency problems in microservice architectures.

## Why do we use it?
```
PROBLEM:
  User reports: "The order page is slow"
  Which service is slow? Gateway? Order-service? User-service? Database?
  Logs are on 5 different servers!

SOLUTION (Zipkin):
  Every request gets a unique TRACE ID
  Each service creates a SPAN (name + start time + duration)
  Zipkin collects all spans → shows the complete journey:
  
  Trace: abc-123-def
  ├── api-gateway         [2ms]  ──────────────────
  ├── order-service      [45ms]     ─────────────────────────
  ├── user-service       [30ms]          ───────────────────
  └── postgresql          [20ms]               ──────────────

  → AHA! PostgreSQL query took 20ms → need to add an index!
```

## How it works:
```
1. Micrometer Tracing (micrometer-tracing-bridge-brave)
   → instruments Spring Boot automatically
   → adds trace ID and span ID to every HTTP request

2. Each service sends spans to Zipkin collector
   → endpoint: http://localhost:9411/api/v2/spans

3. Zipkin stores spans → provides UI to visualize
   → http://localhost:9411
```

## Setup (Mac):
```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
# UI: http://localhost:9411
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 17. MICROMETER + PROMETHEUS (Metrics & Monitoring)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Micrometer is a metrics collection facade (like SLF4J for metrics).
Prometheus is a monitoring system that scrapes and stores metrics.

## Why do we use it?
```
Actuator exposes metrics:
  GET /actuator/health     → { status: "UP" }
  GET /actuator/metrics    → list of all metrics
  GET /actuator/prometheus → metrics in Prometheus format

Prometheus scrapes these endpoints every 15 seconds → stores time-series data
Grafana connects to Prometheus → beautiful dashboards

Key metrics:
  http_server_requests_seconds_count → total requests
  http_server_requests_seconds_sum   → total response time
  jvm_memory_used_bytes              → memory usage
  system_cpu_usage                   → CPU usage
  db_connection_pool_active          → active DB connections
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 18. SPRINGDOC OPENAPI (Swagger UI)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
SpringDoc OpenAPI generates API documentation automatically from your
REST controllers. It provides an interactive Swagger UI to test APIs.

## Why do we use it?
- **Auto-generated**: reads your @RestController annotations → generates docs
- **Interactive**: test APIs directly from the browser
- **Standardized**: follows OpenAPI 3.0 specification
- **Team collaboration**: frontend developers can see all available APIs

## URLs:
```
user-service:  http://localhost:8081/swagger-ui.html
order-service: http://localhost:8082/swagger-ui.html
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 19. LOMBOK
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
Lombok is a Java annotation processor that generates boilerplate code at
compile time (getters, setters, constructors, builders, toString, etc.).

## Why do we use it?
```
WITHOUT Lombok (50 lines):
  public class User {
      private Long id;
      private String username;
      
      public Long getId() { return id; }
      public void setId(Long id) { this.id = id; }
      public String getUsername() { return username; }
      public void setUsername(String username) { this.username = username; }
      
      @Override public String toString() { return "User{id=" + id + ", username=" + username + "}"; }
      @Override public boolean equals(Object o) { /* 10 lines */ }
      @Override public int hashCode() { /* 5 lines */ }
      
      public User() {}
      public User(Long id, String username) { this.id = id; this.username = username; }
      
      public static UserBuilder builder() { /* 20 lines */ }
  }

WITH Lombok (5 lines):
  @Data @Builder @NoArgsConstructor @AllArgsConstructor
  public class User {
      private Long id;
      private String username;
  }
```

## Key annotations:
```
@Data           → getters + setters + toString + equals + hashCode
@Builder        → Builder pattern: User.builder().username("john").build()
@NoArgsConstructor  → empty constructor (required by JPA)
@AllArgsConstructor → constructor with all fields
@RequiredArgsConstructor → constructor with final fields only
@Slf4j          → generates: private static final Logger log = LoggerFactory.getLogger(...)
@Getter/@Setter → individual getters/setters
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# 20. HIKARICP (Connection Pool)
# ═══════════════════════════════════════════════════════════════════════════════

## What is it?
HikariCP is the fastest JDBC connection pool for Java. It's the default
connection pool in Spring Boot.

## Why do we use it?
```
WITHOUT connection pool:
  Request 1: open DB connection (100ms) → query (5ms) → close (10ms) = 115ms
  Request 2: open DB connection (100ms) → query (5ms) → close (10ms) = 115ms
  100 requests = 100 × 115ms = very slow, DB overwhelmed

WITH HikariCP:
  Startup: open 10 connections to DB → keep them in pool
  Request 1: borrow connection from pool (0.01ms) → query (5ms) → return to pool
  Request 2: borrow connection from pool (0.01ms) → query (5ms) → return to pool
  100 requests = connections reused → 100x faster
```

## Configuration:
```
maximumPoolSize: 10    → max 10 connections
minimumIdle: 5         → keep at least 5 idle connections ready
connectionTimeout: 30s → wait max 30 seconds for a connection
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# SERVICE PORT MAP
# ═══════════════════════════════════════════════════════════════════════════════

| Service | Port | Description |
|---|---|---|
| service-registry (Eureka) | 8761 | Service discovery dashboard |
| config-server | 8888 | Centralized configuration |
| api-gateway | 8080 | Single entry point for clients |
| user-service | 8081 | User management + auth |
| order-service | 8082 | Order management |
| PostgreSQL | 5432 | Primary database |
| MySQL | 3306 | Audit database |
| Redis | 6379 | Distributed cache |
| Kafka | 9092 | Event streaming |
| RabbitMQ | 5672 | Message queuing |
| RabbitMQ UI | 15672 | RabbitMQ management dashboard |
| Zipkin | 9411 | Distributed tracing UI |


---

# ═══════════════════════════════════════════════════════════════════════════════
# STARTUP ORDER (Important!)
# ═══════════════════════════════════════════════════════════════════════════════

```
1. Infrastructure (via brew on Mac):
   brew services start postgresql
   brew services start mysql
   brew services start redis
   brew services start zookeeper && brew services start kafka
   brew services start rabbitmq

2. Create databases (first time only):
   psql -U postgres -c "CREATE DATABASE userdb;"
   psql -U postgres -c "CREATE DATABASE orderdb;"
   mysql -u root -e "CREATE DATABASE IF NOT EXISTS user_audit_db;"

3. Build common library:
   cd common-library && ./mvnw clean install -DskipTests

4. Start services IN ORDER:
   ① service-registry (8761) — must be first (others register with it)
   ② config-server (8888) — must be second (others fetch config from it)
   ③ user-service (8081) — business service
   ④ order-service (8082) — business service
   ⑤ api-gateway (8080) — must be last (needs to discover ③ and ④)

5. Verify:
   Eureka Dashboard: http://localhost:8761
   User API: http://localhost:8081/swagger-ui.html
   Order API: http://localhost:8082/swagger-ui.html
   Gateway: http://localhost:8080/actuator/health
```


---

# ═══════════════════════════════════════════════════════════════════════════════
# API ENDPOINTS REFERENCE
# ═══════════════════════════════════════════════════════════════════════════════

## User Service (via Gateway :8080 or direct :8081)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/v1/users | ❌ No | Register new user |
| POST | /api/v1/users/auth/login | ❌ No | Login, get JWT token |
| GET | /api/v1/users | 🔑 JWT | List all users (paginated) |
| GET | /api/v1/users/{id} | 🔑 JWT | Get user by ID |
| GET | /api/v1/users/username/{username} | 🔑 JWT | Get user by username |
| GET | /api/v1/users/active | 🔑 JWT | Get active users |
| GET | /api/v1/users/search?name=john | 🔑 JWT | Search users by name |
| PUT | /api/v1/users/{id} | 🔑 JWT | Update user |
| DELETE | /api/v1/users/{id} | 🔑 JWT+ADMIN | Delete user (admin only) |
| GET | /api/v1/users/reactive/{id} | 🔑 JWT | Get user (reactive) |
| GET | /api/v1/users/reactive | 🔑 JWT | Get all users (reactive) |
| GET | /api/v1/users/reactive/stream | 🔑 JWT | Stream users (SSE) |

## Order Service (via Gateway :8080 or direct :8082)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/v1/orders | 🔑 JWT | Create order |
| GET | /api/v1/orders | 🔑 JWT | List all orders |
| GET | /api/v1/orders/{id} | 🔑 JWT | Get order by ID |
| GET | /api/v1/orders/number/{orderNumber} | 🔑 JWT | Get order by number |
| GET | /api/v1/orders/user/{userId} | 🔑 JWT | Get user's orders |
| PATCH | /api/v1/orders/{id}/status?status=X | 🔑 JWT | Update order status |
| DELETE | /api/v1/orders/{id} | 🔑 JWT | Cancel order |

## Sample Registration Request:
```json
POST http://localhost:8080/api/v1/users
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

## Sample Login Request:
```json
POST http://localhost:8080/api/v1/users/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

## Sample Order Request:
```json
POST http://localhost:8080/api/v1/orders
Authorization: Bearer <jwt_token_from_login>
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productName": "MacBook Pro",
      "quantity": 1,
      "price": 2499.99
    },
    {
      "productName": "USB-C Cable",
      "quantity": 3,
      "price": 19.99
    }
  ],
  "totalAmount": 2559.96,
  "paymentMethod": "CREDIT_CARD"
}
```

