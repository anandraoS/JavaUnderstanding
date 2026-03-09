# Quick Start Guide - Microservices Project

## ⚡ 5-Minute Quick Start

### Prerequisites Installed?
- [ ] Java 17+
- [ ] Maven 3.8+
- [ ] PostgreSQL running
- [ ] MySQL running
- [ ] Redis running
- [ ] Kafka running
- [ ] RabbitMQ running

### Step 1: Quick Database Setup (2 minutes)

**PostgreSQL (via psql or DBeaver):**
```sql
CREATE DATABASE userdb;
CREATE DATABASE orderdb;
```

**MySQL (via MySQL Workbench or CLI):**
```sql
CREATE DATABASE userdb_secondary;
```

### Step 2: Start Services in Order (5 minutes)

**Terminal 1 - Service Registry:**
```bash
cd service-registry
mvn spring-boot:run
# Wait for: "Started ServiceRegistryApplication"
# Check: http://localhost:8761
```

**Terminal 2 - Config Server:**
```bash
cd config-server
mvn spring-boot:run
# Wait for: "Started ConfigServerApplication"
```

**Terminal 3 - Common Library:**
```bash
cd common-library
mvn clean install
```

**Terminal 4 - User Service:**
```bash
cd user-service
mvn spring-boot:run
# Wait for: "Started UserServiceApplication"
```

**Terminal 5 - Order Service:**
```bash
cd order-service
mvn spring-boot:run
# Wait for: "Started OrderServiceApplication"
```

**Terminal 6 - API Gateway:**
```bash
cd api-gateway
mvn spring-boot:run
# Wait for: "Started ApiGatewayApplication"
```

### Step 3: Verify All Services Running (1 minute)

```bash
# Open in browser
http://localhost:8761          # Eureka Dashboard

# All 4 services should show as UP:
# - user-service (8081)
# - order-service (8082)
# - api-gateway (8080)
# - config-server (8888)
```

### Step 4: Test First API (2 minutes)

**Option A: Postman**
1. Import `Postman_Collection.json`
2. Create User (Register User request)
3. Copy JWT token from response
4. Use token in Authorization header for other requests

**Option B: cURL**
```bash
# 1. Create user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!"
  }'

# 2. Login to get token
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Password123!"
  }'

# Response: {"token": "eyJhbGc..."}

# 3. Get user with token
curl -H "Authorization: Bearer eyJhbGc..." \
  http://localhost:8080/api/v1/users/1
```

---

## 🧭 Navigation Guide

### Learning Path (30 minutes)

**5 min - Understand Architecture**
→ Open `README.md` → Read "Architecture Overview"

**5 min - Database & Queue Setup**
→ Open `DATABASE_AND_QUEUE_CONFIG.md` → Copy connection strings

**5 min - Core Concepts**
→ Open `COMPREHENSIVE_CONCEPTS_NOTES.md` → Read "Dependency Injection"

**5 min - API Testing**
→ Import `Postman_Collection.json` → Test User creation

**5 min - Monitoring**
→ Visit http://localhost:8761 (Eureka)
→ Visit http://localhost:8081/swagger-ui.html (Swagger)

**5 min - Review Code**
→ Check `user-service/controller/UserController.java`
→ Understand @RestController, endpoints, error handling

---

## 📂 File Structure

```
javaUnderstanding/
├── README.md                           ← Project overview & setup
├── COMPREHENSIVE_CONCEPTS_NOTES.md     ← Deep dive into all concepts
├── DATABASE_AND_QUEUE_CONFIG.md        ← All connection URLs
├── QUICKSTART.md                       ← This file
├── Postman_Collection.json             ← Import to Postman
│
├── common-library/                     ← Shared code
│   ├── pom.xml
│   └── src/main/java/
│       └── dto/, event/, util/, ...
│
├── service-registry/                   ← Eureka Server (Port 8761)
│   ├── pom.xml
│   ├── application.yaml
│   └── src/main/java/ServiceRegistryApplication.java
│
├── config-server/                      ← Config Server (Port 8888)
│   ├── pom.xml
│   ├── application.yaml
│   ├── src/main/resources/config/
│   │   ├── user-service.yaml
│   │   ├── order-service.yaml
│   │   └── api-gateway.yaml
│   └── src/main/java/ConfigServerApplication.java
│
├── api-gateway/                        ← API Gateway (Port 8080)
│   ├── pom.xml
│   ├── application.yaml
│   └── src/main/java/
│       ├── config/
│       ├── controller/
│       ├── filter/
│       └── ApiGatewayApplication.java
│
├── user-service/                       ← User Service (Port 8081)
│   ├── pom.xml
│   ├── application.yaml
│   └── src/main/java/
│       ├── config/        ← Database, Cache, Security config
│       ├── entity/        ← JPA entities
│       ├── repository/    ← Database access
│       ├── service/       ← Business logic
│       ├── controller/    ← REST endpoints
│       ├── exception/     ← Error handling
│       └── UserServiceApplication.java
│
└── order-service/                      ← Order Service (Port 8082)
    ├── pom.xml
    ├── application.yaml
    └── src/main/java/
        ├── config/       ← Circuit breaker, RabbitMQ, WebClient
        ├── entity/       ← JPA entities
        ├── repository/   ← Database access
        ├── service/      ← Business logic with resilience
        ├── controller/   ← REST endpoints
        ├── listener/     ← Kafka & RabbitMQ listeners
        └── OrderServiceApplication.java
```

---

## 🎯 What Each Service Does

### Service Registry (Eureka)
```
Purpose: Keeps track of all services
Port: 8761
Health: http://localhost:8761
Why: Services discover each other dynamically
```

### Config Server
```
Purpose: Central configuration management
Port: 8888
Stores: Database URLs, message broker URLs, JWT secrets
Why: Change config without restarting services
```

### API Gateway
```
Purpose: Single entry point for all clients
Port: 8080
Features: 
  - JWT authentication
  - Route to correct service
  - Rate limiting
  - Circuit breaker
Why: Clients don't need to know service addresses
```

### User Service
```
Purpose: Manage users and authentication
Port: 8081
Database: PostgreSQL (users) + MySQL (audit logs)
APIs:
  - POST /api/v1/users → Create user
  - POST /api/v1/users/auth/login → Login (get JWT)
  - GET /api/v1/users/{id} → Get user
  - PUT /api/v1/users/{id} → Update user
  - DELETE /api/v1/users/{id} → Delete user
Features:
  - Multiple databases
  - Redis cache
  - Reactive endpoints
  - JWT security
  - Kafka events
```

### Order Service
```
Purpose: Manage orders
Port: 8082
Database: PostgreSQL
APIs:
  - POST /api/v1/orders → Create order
  - GET /api/v1/orders/{id} → Get order
  - PATCH /api/v1/orders/{id}/status → Update status
  - DELETE /api/v1/orders/{id} → Cancel order
Features:
  - Circuit breaker (calls user-service)
  - RabbitMQ notifications
  - Kafka events
  - Async processing
  - Caching
```

---

## 🧪 Common Test Scenarios

### Scenario 1: Create User → View in Database

```bash
# 1. Create user via API
POST http://localhost:8080/api/v1/users
{
  "username": "john",
  "email": "john@test.com",
  "password": "Password123!"
}

# 2. Check in PostgreSQL
psql -U postgres -d userdb
SELECT * FROM users;

# 3. Check cache in Redis
redis-cli
KEYS *
GET users::1
```

### Scenario 2: Create Order → Check Events

```bash
# 1. Create order via API
POST http://localhost:8080/api/v1/orders
{
  "userId": 1,
  "paymentMethod": "CREDIT_CARD",
  "items": [{"productName": "Laptop", "quantity": 1, "price": 999.99}]
}

# 2. View Kafka event
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning

# 3. Check RabbitMQ queue
# Visit: http://localhost:15672 → Queues → notification.queue
```

### Scenario 3: Test Circuit Breaker

```bash
# 1. Stop user-service
# Ctrl+C in user service terminal

# 2. Try to create order
POST http://localhost:8080/api/v1/orders
# Response: "User service is temporarily unavailable" (fallback)

# 3. Check circuit breaker status
GET http://localhost:8082/actuator/circuitbreakers

# 4. Restart user-service
# It will recover automatically
```

### Scenario 4: View Distributed Trace

```bash
# 1. Start Zipkin (if Docker installed)
docker run -d -p 9411:9411 openzipkin/zipkin

# 2. Make API call through gateway
GET http://localhost:8080/api/v1/users/1

# 3. View trace in Zipkin
# http://localhost:9411
# You'll see the request flow across services
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| "Port already in use" | Kill process or change port in application.yaml |
| "Eureka shows DOWN" | Wait 30 seconds, refresh http://localhost:8761 |
| "Database connection failed" | Check if PostgreSQL/MySQL running, verify credentials |
| "Circuit breaker always OPEN" | Check if dependent service running, review logs |
| "No JWT token" | Login first to get token, include in Authorization header |
| "Kafka connection timeout" | Start Kafka with Zookeeper first |
| "RabbitMQ connection refused" | Docker: `docker start rabbitmq` |

---

## 🔑 Key Endpoints

| Method | Endpoint | Purpose | Requires Auth |
|--------|----------|---------|---------------|
| POST | /api/v1/users | Create user | ✗ |
| POST | /api/v1/users/auth/login | Get JWT token | ✗ |
| GET | /api/v1/users/{id} | Get user details | ✓ |
| GET | /api/v1/users | List users (paginated) | ✓ |
| PUT | /api/v1/users/{id} | Update user | ✓ |
| DELETE | /api/v1/users/{id} | Delete user (admin) | ✓ Admin |
| POST | /api/v1/orders | Create order | ✓ |
| GET | /api/v1/orders/{id} | Get order | ✓ |
| GET | /api/v1/orders | List orders | ✓ |
| PATCH | /api/v1/orders/{id}/status | Update order status | ✓ |
| DELETE | /api/v1/orders/{id} | Cancel order | ✓ |

---

## 📊 Useful Monitoring URLs

```
Eureka Dashboard      http://localhost:8761
User Service Health   http://localhost:8081/actuator/health
Order Service Health  http://localhost:8082/actuator/health
User Service Swagger  http://localhost:8081/swagger-ui.html
Order Service Swagger http://localhost:8082/swagger-ui.html
RabbitMQ UI           http://localhost:15672 (guest/guest)
Zipkin Traces         http://localhost:9411
Prometheus Metrics    http://localhost:8081/actuator/prometheus
```

---

## 🚀 Next Steps

1. **Understand Architecture** (15 min)
   - Read README.md architecture section
   - Understand why we have 6 services

2. **Learn Core Concepts** (30 min)
   - Read COMPREHENSIVE_CONCEPTS_NOTES.md
   - Focus on sections relevant to your role

3. **Explore Code** (1 hour)
   - Start with UserController.java
   - Review UserService.java (business logic)
   - Check repositories (database access)

4. **Test APIs** (30 min)
   - Use Postman_Collection.json
   - Create user, login, create order
   - Monitor requests in Zipkin

5. **Deep Dive** (2+ hours)
   - Modify code to understand changes
   - Create new endpoints
   - Add new features

---

## 💡 Learning Tips

- **Read Code Comments**: Every class has detailed JavaDoc
- **Follow Logs**: Enable DEBUG logging to see what's happening
- **Use Postman**: Visual testing is easier than cURL
- **Check Actuator**: Health & metrics endpoints show real-time status
- **Monitor Kafka**: See what events are published
- **Watch Zipkin**: Trace requests across services
- **Review Config**: Check config files to understand properties

---

## 📚 Project Files Summary

| File | Purpose | Read First |
|------|---------|-----------|
| README.md | Project overview & setup instructions | ✓ |
| COMPREHENSIVE_CONCEPTS_NOTES.md | Deep technical concepts | ✓ |
| DATABASE_AND_QUEUE_CONFIG.md | All connection URLs & configs | ✓ |
| Postman_Collection.json | API test collection | ✓ |
| ARCHITECTURE_DECISIONS.md | Why we chose these technologies | ○ |

✓ = Read first
○ = Read if interested

---

**Start with README.md → Run services → Test APIs → Study code → Explore configs**

Happy Learning! 🎓

