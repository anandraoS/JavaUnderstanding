# Database Connection URLs & Configuration Guide

## 📊 Database Connections

### PostgreSQL - Primary Database

**Connection Details:**
```
Host: localhost
Port: 5432
Username: postgres
Password: postgres
```

**JDBC URLs:**

```properties
# User Service - Primary DB
spring.datasource.primary.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.primary.username=postgres
spring.datasource.primary.password=postgres
spring.datasource.primary.driver-class-name=org.postgresql.Driver

# Order Service DB
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

**HikariCP Configuration:**
```yaml
spring:
  datasource:
    primary:
      hikari:
        maximum-pool-size: 10
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
        auto-commit: true
```

**DDL Auto Settings:**
```
spring.jpa.hibernate.ddl-auto=update  # Auto-create/update tables
```

### MySQL - Secondary Database (Audit Logs)

**Connection Details:**
```
Host: localhost
Port: 3306
Username: root
Password: root
```

**JDBC URL:**
```properties
# User Service - Secondary DB (Audit)
spring.datasource.secondary.url=jdbc:mysql://localhost:3306/userdb_secondary
spring.datasource.secondary.username=root
spring.datasource.secondary.password=root
spring.datasource.secondary.driver-class-name=com.mysql.cj.jdbc.Driver
```

**HikariCP Configuration:**
```yaml
spring:
  datasource:
    secondary:
      hikari:
        maximum-pool-size: 5
        minimum-idle: 2
        connection-timeout: 30000
```

### Database Setup Scripts

**PostgreSQL:**
```sql
-- Connect to default database
\c postgres

-- Create databases
CREATE DATABASE userdb WITH ENCODING 'UTF8';
CREATE DATABASE orderdb WITH ENCODING 'UTF8';

-- Create user
CREATE USER postgres WITH PASSWORD 'postgres';

-- Grant privileges
ALTER ROLE postgres SUPERUSER;
GRANT ALL PRIVILEGES ON DATABASE userdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO postgres;

-- Verify
\l
\du
```

**MySQL:**
```sql
-- Create database for audit logs
CREATE DATABASE userdb_secondary 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';

-- Grant privileges
GRANT ALL PRIVILEGES ON userdb_secondary.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
SHOW GRANTS FOR 'root'@'localhost';
```

---

## 🔴 Redis Configuration

**Connection Details:**
```
Host: localhost
Port: 6379
Password: (none by default)
```

**Configuration:**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60000  # Connection timeout in ms
      database: 0      # Default database
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

**Redis Commands:**
```bash
# Connect to Redis
redis-cli

# View all keys
KEYS *

# Check cache
GET users::1
GET orders::1

# Clear cache
FLUSHDB
FLUSHALL

# Monitor operations
MONITOR
```

**Cache Configuration in Application:**
```java
// Enable caching
@EnableCaching

// Cache annotations
@Cacheable(value = "users", key = "#id")     // Get from cache
@CachePut(value = "users", key = "#id")      // Update cache
@CacheEvict(value = "users", key = "#id")    // Clear cache
```

---

## 📨 Message Broker URLs

### Kafka Configuration

**Bootstrap Servers:**
```properties
spring.kafka.bootstrap-servers=localhost:9092
```

**Producer Configuration:**
```yaml
spring:
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
      properties:
        linger.ms: 10
        batch.size: 16384
```

**Consumer Configuration:**
```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: user-service-group
      auto-offset-reset: earliest
      enable-auto-commit: true
      auto-commit-interval-ms: 1000
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

**Topics:**
```
user-events      # User CRUD events
order-events     # Order lifecycle events
```

**Kafka Commands:**
```bash
# List topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Create topic
kafka-topics.sh --create --topic user-events --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1

# Describe topic
kafka-topics.sh --describe --topic user-events --bootstrap-server localhost:9092

# Consume messages
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic user-events --from-beginning

# Monitor lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group user-service-group --describe
```

### RabbitMQ Configuration

**Connection Details:**
```
Host: localhost
Port: 5672 (AMQP)
Port: 15672 (Management UI)
Username: guest
Password: guest
```

**Configuration:**
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    connection-timeout: 10000
    channel-cache-size: 25
```

**Queue Configuration:**
```java
// Queues
queue.name = order.queue
queue.name = notification.queue

// Exchanges
exchange.name = order.exchange
exchange.name = notification.exchange

// Routing Keys
routing.key = order.routing.key
routing.key = notification.routing.key
```

**RabbitMQ Management UI:**
```
URL: http://localhost:15672
Username: guest
Password: guest
```

**RabbitMQ CLI Commands:**
```bash
# List queues
rabbitmqctl list_queues

# List exchanges
rabbitmqctl list_exchanges

# List bindings
rabbitmqctl list_bindings

# Purge queue
rabbitmqctl purge_queue order.queue

# View user
rabbitmqctl list_users

# Check connections
rabbitmqctl list_connections
```

---

## 🔐 Security - JWT Configuration

**JWT Constants:**
```properties
# 5 hours expiration
jwt.expiration=18000000

# Secret key (use environment variable in production)
jwt.secret=MySecretKeyForJWTTokenGenerationMustBeLongEnoughFor512BitHS512AlgorithmRequirement
```

**Token Structure:**
```
Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "role": "ROLE_USER",
  "iat": 1234567890,
  "exp": 1234651890
}

Signature: HMACSHA512(header.payload, secret)
```

---

## 📡 Service URLs & Ports

| Service | Port | URL | Health |
|---------|------|-----|--------|
| Service Registry | 8761 | http://localhost:8761 | (Dashboard) |
| Config Server | 8888 | http://localhost:8888 | /actuator/health |
| API Gateway | 8080 | http://localhost:8080 | /actuator/health |
| User Service | 8081 | http://localhost:8081 | /actuator/health |
| Order Service | 8082 | http://localhost:8082 | /actuator/health |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432 | psql |
| MySQL | 3306 | jdbc:mysql://localhost:3306 | mysql |
| Redis | 6379 | redis://localhost:6379 | redis-cli |
| Kafka | 9092 | localhost:9092 | - |
| Zookeeper | 2181 | localhost:2181 | - |
| RabbitMQ | 5672 | amqp://localhost:5672 | /15672 |
| Zipkin | 9411 | http://localhost:9411 | (Dashboard) |

---

## 🔄 Resilience4j Configuration

**Circuit Breaker:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        register-health-indicator: true
        sliding-window-size: 10           # Last 10 calls
        failure-rate-threshold: 50        # Fail if 50% fail
        wait-duration-in-open-state: 10s  # Wait before retry
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5
```

**Retry:**
```yaml
resilience4j:
  retry:
    instances:
      userService:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

**Time Limiter:**
```yaml
resilience4j:
  timelimiter:
    instances:
      userService:
        timeout-duration: 3s
```

---

## 📊 Monitoring URLs

| Tool | URL | Purpose |
|------|-----|---------|
| Zipkin | http://localhost:9411 | Distributed tracing |
| RabbitMQ | http://localhost:15672 | Queue management |
| Prometheus | http://localhost:9090 | Metrics collection |
| Grafana | http://localhost:3000 | Metrics visualization |

---

## 🧪 Connection Testing

**Test PostgreSQL:**
```bash
psql -h localhost -U postgres -d userdb
# Then: SELECT 1;
```

**Test MySQL:**
```bash
mysql -h localhost -u root -p
# Enter password: root
# Then: USE userdb_secondary;
```

**Test Redis:**
```bash
redis-cli
# Then: PING
```

**Test Kafka:**
```bash
kafka-broker-api-versions.sh --bootstrap-server localhost:9092
```

**Test RabbitMQ:**
```bash
rabbitmqctl status
```

---

## 📝 Environment Variables (for Production)

```bash
# Database
DB_PRIMARY_URL=jdbc:postgresql://prod-db:5432/userdb
DB_PRIMARY_USER=prod_user
DB_PRIMARY_PASSWORD=${DB_PRIMARY_PASSWORD}

DB_SECONDARY_URL=jdbc:mysql://audit-db:3306/userdb_secondary
DB_SECONDARY_USER=audit_user
DB_SECONDARY_PASSWORD=${DB_SECONDARY_PASSWORD}

# Redis
REDIS_HOST=redis-cluster
REDIS_PORT=6379
REDIS_PASSWORD=${REDIS_PASSWORD}

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092,kafka-2:9092,kafka-3:9092

# RabbitMQ
RABBITMQ_HOST=rabbitmq-cluster
RABBITMQ_PORT=5672
RABBITMQ_USER=${RABBITMQ_USER}
RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD}

# JWT
JWT_SECRET=${JWT_SECRET}

# Zipkin
ZIPKIN_URL=http://zipkin:9411
```

---

## 🚀 Quick Docker Setup

```bash
# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:14

# MySQL
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -p 3306:3306 \
  mysql:8

# Redis
docker run -d --name redis \
  -p 6379:6379 \
  redis:7

# Kafka with Zookeeper
docker run -d --name kafka \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
  -p 9092:9092 \
  confluentinc/cp-kafka:7.0.0

# RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Zipkin
docker run -d --name zipkin \
  -p 9411:9411 \
  openzipkin/zipkin
```

---

**Note**: Update all passwords and secrets in production. Use AWS Secrets Manager, Azure Key Vault, or HashiCorp Vault for secret management.

