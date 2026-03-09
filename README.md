# Comprehensive Spring Boot Microservices Architecture

## 🎯 Project Overview

This is a **production-grade microservices architecture** demonstrating all enterprise Spring Boot and microservices concepts for a professional with 12 years of experience.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Services Description](#services-description)
3. [Technologies & Concepts Covered](#technologies--concepts-covered)
4. [Prerequisites](#prerequisites)
5. [Database Setup](#database-setup)
6. [Message Broker Setup](#message-broker-setup)
7. [Running the Services](#running-the-services)
8. [Testing the APIs](#testing-the-apis)
9. [Monitoring & Observability](#monitoring--observability)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway (8080)                       │
│  ┌──────────────┬──────────────┬──────────────┬───────────────┐ │
│  │ JWT Auth     │ Rate Limiting│ Circuit      │ Load          │ │
│  │ Filter       │              │ Breaker      │ Balancer      │ │
│  └──────────────┴──────────────┴──────────────┴───────────────┘ │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                ┌───────────┴───────────┐
                │                       │
      ┌─────────▼─────────┐   ┌────────▼─────────┐
      │  User Service     │   │  Order Service   │
      │     (8081)        │   │     (8082)       │
      │                   │   │                  │
      │ ┌───────────────┐ │   │ ┌──────────────┐│
      │ │PostgreSQL(PRI)│ │   │ │ PostgreSQL   ││
      │ │MySQL (SEC)    │ │   │ │              ││
      │ │Redis Cache    │ │   │ │ Redis Cache  ││
      │ │Kafka Producer │ │   │ │ Kafka        ││
      │ │WebFlux (React)│ │   │ │ RabbitMQ     ││
      │ └───────────────┘ │   │ │ Circuit      ││
      └───────────────────┘   │ │ Breaker      ││
                              │ └──────────────┘│
                              └──────────────────┘
                                       │
                    ┌──────────────────┴───────────────────┐
                    │                                      │
          ┌─────────▼─────────┐              ┌────────────▼──────────┐
          │  Service Registry  │              │   Config Server      │
          │  Eureka (8761)     │              │      (8888)          │
          └────────────────────┘              └──────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    External Infrastructure                           │
│  ┌────────────┬─────────────┬──────────────┬────────────────────┐  │
│  │ PostgreSQL │   MySQL     │    Redis     │  Message Brokers  │  │
│  │  (5432)    │   (3306)    │   (6379)     │ Kafka(9092)       │  │
│  │            │             │              │ RabbitMQ(5672)    │  │
│  └────────────┴─────────────┴──────────────┴────────────────────┘  │
│  ┌────────────┬─────────────────────────────────────────────────┐  │
│  │ Zipkin     │            Prometheus & Grafana                 │  │
│  │  (9411)    │       (Monitoring & Metrics)                    │  │
│  └────────────┴─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Services Description

### 1. **Service Registry (Eureka Server)** - Port 8761
- **Purpose**: Service discovery and registration
- **Concepts**: Service Discovery, Load Balancing, High Availability
- **URL**: http://localhost:8761

### 2. **Config Server** - Port 8888
- **Purpose**: Centralized configuration management
- **Concepts**: Externalized configuration, Environment-specific configs
- **Features**: Native/Git backend, Dynamic refresh

### 3. **API Gateway** - Port 8080
- **Purpose**: Single entry point for all microservices
- **Features**:
  - JWT Authentication
  - Circuit Breaker (Resilience4j)
  - Rate Limiting (Redis)
  - Request/Response logging
  - CORS configuration
  - Fallback mechanisms

### 4. **User Service** - Port 8081
- **Purpose**: User management and authentication
- **Features**:
  - **Multiple Datasources**: PostgreSQL (primary) + MySQL (audit)
  - **REST APIs**: CRUD operations with pagination/sorting
  - **Reactive APIs**: WebFlux with Mono/Flux
  - **Caching**: Redis for performance
  - **Security**: JWT authentication, BCrypt password encoding
  - **Async Processing**: @Async for audit logs
  - **Event Publishing**: Kafka producer
  - **Scheduled Tasks**: Cache cleanup, reports
  - **Monitoring**: Actuator, Prometheus metrics

### 5. **Order Service** - Port 8082
- **Purpose**: Order management and processing
- **Features**:
  - **Circuit Breaker**: Resilience4j for user service calls
  - **Retry Mechanism**: Automatic retries with exponential backoff
  - **Inter-service Communication**: WebClient with load balancing
  - **Message Brokers**:
    - Kafka: Event publishing
    - RabbitMQ: Notification queue
  - **Async Processing**: Order processing
  - **Caching**: Redis
  - **Event Consumption**: Kafka listener for user events

### 6. **Common Library**
- **Purpose**: Shared components across services
- **Contents**:
  - DTOs (Data Transfer Objects)
  - Event models
  - Utility classes (JWT)
  - Constants
  - Custom exceptions
  - OpenAPI configuration

---

## 🚀 Technologies & Concepts Covered

### **Spring Framework Core**
✅ Dependency Injection (DI) & Inversion of Control (IoC)
✅ Bean lifecycle, scopes, and configurations
✅ ApplicationContext & BeanFactory
✅ Component scanning

### **Spring Boot**
✅ Auto-configuration
✅ Starters
✅ Configuration properties (@ConfigurationProperties)
✅ Actuator for monitoring
✅ DevTools

### **Spring Data**
✅ JPA with Hibernate
✅ Multiple datasource configuration
✅ Repository pattern (JpaRepository)
✅ Custom queries (JPQL, Native SQL)
✅ Pagination and sorting
✅ Entity relationships (@OneToMany, @ManyToOne)

### **Spring MVC**
✅ RESTful API development
✅ @RestController, @RequestMapping
✅ Request/Response handling
✅ Content negotiation
✅ Exception handling (@RestControllerAdvice)
✅ Validation (@Valid, JSR-303)

### **Spring Security**
✅ Authentication & Authorization
✅ JWT token generation and validation
✅ BCrypt password encoding
✅ Method-level security (@PreAuthorize)
✅ Security filter chain

### **Microservices Architecture**
✅ Service Registry (Eureka)
✅ Service Discovery
✅ API Gateway pattern
✅ Circuit Breaker (Resilience4j)
✅ Retry mechanisms
✅ Fallback strategies
✅ Load balancing
✅ Centralized configuration

### **Asynchronous Programming**
✅ Event-driven architecture
✅ Message brokers (Kafka, RabbitMQ)
✅ Publisher/Subscriber pattern
✅ @Async annotation
✅ ThreadPoolTaskExecutor
✅ CompletableFuture

### **Reactive Programming**
✅ Project Reactor
✅ WebFlux
✅ Mono & Flux
✅ Backpressure handling
✅ Non-blocking I/O
✅ Server-Sent Events (SSE)

### **Caching**
✅ Spring Cache abstraction
✅ Redis caching
✅ @Cacheable, @CachePut, @CacheEvict
✅ Cache configuration

### **Distributed Systems**
✅ Inter-service communication (REST, WebClient)
✅ Distributed tracing (Zipkin, Sleuth/Micrometer)
✅ Correlation ID propagation
✅ Event sourcing

### **Monitoring & Observability**
✅ Actuator endpoints
✅ Health checks (liveness/readiness)
✅ Prometheus metrics
✅ Distributed tracing (Zipkin)
✅ Logging patterns

### **Scheduled Tasks**
✅ @Scheduled annotation
✅ Cron expressions
✅ Fixed rate/delay
✅ Task execution

### **Testing**
✅ Unit testing (JUnit 5)
✅ Integration testing
✅ MockMvc
✅ TestRestTemplate
✅ Reactor testing

### **API Documentation**
✅ Swagger/OpenAPI 3.0
✅ Interactive API docs

---

## 📋 Prerequisites

Before running the services, install the following:

1. **Java 17** or higher
2. **Maven 3.8+**
3. **PostgreSQL 14+**
4. **MySQL 8+**
5. **Redis 7+**
6. **Apache Kafka 3+**
7. **RabbitMQ 3.11+**
8. **Zipkin** (Optional for tracing)

---

## 🗄️ Database Setup

### PostgreSQL Setup

```sql
-- Create databases
CREATE DATABASE userdb;
CREATE DATABASE orderdb;

-- Create user (if needed)
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE userdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO postgres;
```

### MySQL Setup

```sql
-- Create database for user audit
CREATE DATABASE userdb_secondary;

-- Create user (if needed)
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON userdb_secondary.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### Redis Setup

```bash
# Windows (if installed)
redis-server

# Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

---

## 📨 Message Broker Setup

### Kafka Setup

```bash
# Download Kafka from https://kafka.apache.org/downloads

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties  # Linux/Mac
bin\windows\zookeeper-server-start.bat config\zookeeper.properties  # Windows

# Start Kafka
bin/kafka-server-start.sh config/server.properties  # Linux/Mac
bin\windows\kafka-server-start.bat config\server.properties  # Windows

# Docker alternative
docker run -d -p 2181:2181 -p 9092:9092 \
  --name kafka \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=localhost:2181 \
  bitnami/kafka:latest
```

### RabbitMQ Setup

```bash
# Docker (recommended)
docker run -d -p 5672:5672 -p 15672:15672 \
  --name rabbitmq \
  rabbitmq:3-management

# Management UI: http://localhost:15672
# Default credentials: guest/guest
```

### Zipkin Setup (Optional)

```bash
# Docker
docker run -d -p 9411:9411 --name zipkin openzipkin/zipkin

# UI: http://localhost:9411
```

---

## ▶️ Running the Services

### Step-by-Step Startup Sequence

**IMPORTANT**: Services must be started in this order:

```bash
# 1. Start Service Registry (Eureka)
cd service-registry
mvn clean install
mvn spring-boot:run

# Wait for Eureka to start (http://localhost:8761)

# 2. Start Config Server
cd config-server
mvn clean install
mvn spring-boot:run

# Wait for Config Server to start

# 3. Build Common Library
cd common-library
mvn clean install

# 4. Start User Service
cd user-service
mvn clean install
mvn spring-boot:run

# 5. Start Order Service
cd order-service
mvn clean install
mvn spring-boot:run

# 6. Start API Gateway
cd api-gateway
mvn clean install
mvn spring-boot:run
```

### Verify Services are Running

1. **Eureka Dashboard**: http://localhost:8761
   - Should show: user-service, order-service, api-gateway, config-server

2. **Actuator Health Checks**:
   - User Service: http://localhost:8081/actuator/health
   - Order Service: http://localhost:8082/actuator/health
   - API Gateway: http://localhost:8080/actuator/health

3. **Swagger UI**:
   - User Service: http://localhost:8081/swagger-ui.html
   - Order Service: http://localhost:8082/swagger-ui.html

---

## 🧪 Testing the APIs

### 1. Create a User (via Gateway)

```bash
POST http://localhost:8080/api/v1/users/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### 2. Login

```bash
POST http://localhost:8080/api/v1/users/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}

# Response will contain JWT token
```

### 3. Get User (with token)

```bash
GET http://localhost:8080/api/v1/users/1
Authorization: Bearer <your-jwt-token>
```

### 4. Create Order

```bash
POST http://localhost:8080/api/v1/orders
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "userId": 1,
  "paymentMethod": "CREDIT_CARD",
  "items": [
    {
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    },
    {
      "productName": "Mouse",
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

### 5. Get Orders

```bash
GET http://localhost:8080/api/v1/orders?page=0&size=10
Authorization: Bearer <your-jwt-token>
```

### 6. Reactive API (Stream)

```bash
GET http://localhost:8081/api/v1/users/reactive/stream
Accept: text/event-stream
```

---

## 📊 Monitoring & Observability

### Actuator Endpoints

```bash
# Health Check
GET http://localhost:8081/actuator/health

# Metrics
GET http://localhost:8081/actuator/metrics

# Prometheus Metrics
GET http://localhost:8081/actuator/prometheus

# Circuit Breakers State
GET http://localhost:8082/actuator/circuitbreakers

# Cache Info
GET http://localhost:8081/actuator/caches
```

### Zipkin Tracing

1. Start Zipkin: http://localhost:9411
2. Make API calls through Gateway
3. View traces in Zipkin UI
4. See correlation across services

### Kafka Topics

```bash
# List topics
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Consume user events
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic user-events \
  --from-beginning

# Consume order events
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning
```

### RabbitMQ Management

1. Open: http://localhost:15672
2. Login: guest/guest
3. View queues: `order.queue`, `notification.queue`
4. Monitor messages

---

## 🎓 Learning Path

For deep understanding, explore in this order:

1. **Service Registry**: Understand service discovery
2. **Config Server**: Learn centralized configuration
3. **Common Library**: Study shared components
4. **User Service**: Master JPA, Security, Caching, Reactive
5. **Order Service**: Learn Circuit Breaker, Retry, Message Brokers
6. **API Gateway**: Understand routing, filtering, security
7. **Integration**: See how everything works together

---

## 📝 Key Configuration Files

- `application.yaml` - Bootstrap configuration (each service)
- `config-server/src/main/resources/config/*.yaml` - Service configurations
- `pom.xml` - Dependencies management

---

## 🔒 Security Notes

- JWT tokens expire in 5 hours
- Passwords are encrypted with BCrypt
- API Gateway validates all tokens
- Method-level security with @PreAuthorize

---

## 🚧 Troubleshooting

**Services won't start:**
- Check if all prerequisites are installed
- Verify database connections
- Ensure Kafka and RabbitMQ are running
- Check port conflicts

**Eureka shows service as DOWN:**
- Wait 30 seconds for registration
- Check network connectivity
- Verify eureka.client.service-url

**Circuit breaker always OPEN:**
- Check if dependent service is running
- Review Resilience4j configuration
- Check logs for errors

---

## 📚 Additional Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- Resilience4j: https://resilience4j.readme.io
- Kafka: https://kafka.apache.org/documentation/

---

**Made with ❤️ for learning enterprise Spring Boot & Microservices**

