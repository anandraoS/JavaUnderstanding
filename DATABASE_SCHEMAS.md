# Database Schemas & Entity Relationships

## Overview

This document shows all database tables and their relationships.

---

## PostgreSQL - UserDB (User Service - Primary)

```sql
-- User Table (Main entity)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_role ON users(role);
```

**Entity Class:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    private String lastName;
    
    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**Sample Data:**
```sql
INSERT INTO users (username, email, password, first_name, last_name, role, active)
VALUES (
    'johndoe',
    'john@example.com',
    '$2a$10$...',  -- BCrypt encrypted
    'John',
    'Doe',
    'ROLE_USER',
    true
);

INSERT INTO users (username, email, password, role, active)
VALUES (
    'admin',
    'admin@example.com',
    '$2a$10$...',
    'ROLE_ADMIN',
    true
);
```

---

## PostgreSQL - OrderDB (Order Service)

```sql
-- Order Table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,  -- References user in user-service DB
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    subtotal NUMERIC(19, 2) NOT NULL,
    
    CONSTRAINT fk_order_items_order 
        FOREIGN KEY (order_id) 
        REFERENCES orders(id) 
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

**Entity Classes:**
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private Long userId;  // Foreign reference (different DB)
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    private String paymentMethod;
    
    @CreationTimestamp
    private LocalDateTime orderDate;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
}
```

**Sample Data:**
```sql
-- Create order
INSERT INTO orders (order_number, user_id, total_amount, status, payment_method)
VALUES ('ORD-ABC12345', 1, 1349.97, 'PENDING', 'CREDIT_CARD');

-- Add items to order
INSERT INTO order_items (order_id, product_name, quantity, price, subtotal)
VALUES 
(1, 'Laptop', 1, 999.99, 999.99),
(1, 'Mouse', 2, 49.99, 99.98),
(1, 'USB Cable', 3, 19.99, 59.97);

-- Order Status Updates
UPDATE orders SET status = 'PROCESSING', updated_at = CURRENT_TIMESTAMP WHERE id = 1;
UPDATE orders SET status = 'COMPLETED', updated_at = CURRENT_TIMESTAMP WHERE id = 1;
```

---

## MySQL - userdb_secondary (User Service - Audit)

```sql
-- User Audit Table (for historical tracking)
CREATE TABLE user_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,  -- CREATED, UPDATED, DELETED
    details VARCHAR(500),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_audit_user_id (user_id),
    INDEX idx_user_audit_username (username),
    INDEX idx_user_audit_action (action),
    INDEX idx_user_audit_performed_at (performed_at)
);
```

**Entity Class:**
```java
@Entity
@Table(name = "user_audit")
public class UserAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(length = 50)
    private String username;
    
    @Column(length = 20)
    private String action;  // CREATED, UPDATED, DELETED
    
    @Column(length = 500)
    private String details;
    
    @Column(name = "performed_at")
    private LocalDateTime performedAt;
}
```

**Sample Data:**
```sql
-- Audit logs are automatically created async
INSERT INTO user_audit (user_id, username, action, details, performed_at)
VALUES (1, 'johndoe', 'CREATED', 'User account created', NOW());

INSERT INTO user_audit (user_id, username, action, details, performed_at)
VALUES (1, 'johndoe', 'UPDATED', 'User profile updated - email changed', NOW());
```

---

## Redis Schema (Caching)

**Cache Keys:**
```
users::<id>           → User object (TTL: 10 minutes)
orders::<id>          → Order object (TTL: 10 minutes)
orders::user::<uid>   → User's orders list (TTL: 5 minutes)
```

**Example Redis Commands:**
```bash
# Set cache
SET users::1 '{"id":1,"username":"johndoe",...}' EX 600

# Get cache
GET users::1
# Returns: {"id":1,"username":"johndoe",...}

# Delete cache
DEL users::1

# Clear all caches
FLUSHDB

# Monitor cache hits/misses
KEYS *
DBSIZE
```

**Spring Cache Integration:**
```java
@Cacheable(value = "users", key = "#id")
public UserDTO getUserById(Long id) {
    // Cache key: "users::1"
    return userRepository.findById(id);
}

@CachePut(value = "users", key = "#id")
public UserDTO updateUser(Long id, UserDTO dto) {
    // Update cache after write
}

@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    // Remove from cache
}
```

---

## Kafka Topics (Message Streams)

### Topic: user-events
**Purpose**: Stream of user-related events
**Partitions**: 3 (for parallelism)
**Retention**: 7 days (by default)
**Consumer Group**: user-service-group, order-service-group

**Event Format:**
```json
{
  "userId": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "eventType": "USER_CREATED",
  "timestamp": "2024-03-09T10:30:00Z"
}
```

**Event Types:**
- USER_CREATED
- USER_UPDATED
- USER_DELETED

### Topic: order-events
**Purpose**: Stream of order-related events
**Partitions**: 3
**Retention**: 7 days
**Consumer Group**: order-service-group

**Event Format:**
```json
{
  "orderId": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "totalAmount": 1349.97,
  "status": "PENDING",
  "eventType": "ORDER_CREATED",
  "timestamp": "2024-03-09T10:35:00Z"
}
```

**Event Types:**
- ORDER_CREATED
- ORDER_UPDATED
- ORDER_CANCELLED

---

## RabbitMQ Queues

### Queue: order.queue
**Exchange**: order.exchange
**Routing Key**: order.routing.key
**Purpose**: Distribute order notifications

**Message Format:**
```json
{
  "orderId": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "status": "PENDING",
  "totalAmount": 1349.97
}
```

### Queue: notification.queue
**Exchange**: notification.exchange
**Routing Key**: notification.routing.key
**Purpose**: Send notifications (email, SMS, etc.)

**Message Format:**
```json
{
  "orderId": 1,
  "orderNumber": "ORD-ABC12345",
  "userId": 1,
  "type": "ORDER_CREATED",
  "recipientEmail": "john@example.com"
}
```

---

## ER Diagram

### User Service Databases

```
PostgreSQL (userdb)          MySQL (userdb_secondary)
┌─────────────┐              ┌──────────────┐
│    users    │              │ user_audit   │
├─────────────┤              ├──────────────┤
│ id (PK)     │──┐           │ id (PK)      │
│ username    │  │  Async    │ user_id (FK) │
│ email       │  │  Update   │ username     │
│ password    │  │─ ─ ─ ─ ─→ │ action       │
│ first_name  │  │           │ details      │
│ last_name   │  │           │ performed_at │
│ role        │  │           └──────────────┘
│ active      │  │
│ created_at  │  │
│ updated_at  │  │
└─────────────┘  │
                 │
                 │
        Event Published to Kafka
```

### Order Service Database

```
PostgreSQL (orderdb)
┌──────────────┐
│    orders    │
├──────────────┤
│ id (PK)      │
│ order_number │  ┌────────────────────────┐
│ user_id (FK) │─→│ User Service (REST)    │
│ total_amount │  │ Validate user exists   │
│ status       │  │ (Circuit breaker)      │
│ payment_...  │  └────────────────────────┘
│ order_date   │
│ updated_at   │  ┌────────────────────────┐
└──────────────┘─→│ Published to Kafka:    │
                  │ order-events topic     │
         │        └────────────────────────┘
         │
         └──→ ┌──────────────────┐
             │ order_items      │
             ├──────────────────┤
             │ id (PK)          │
             │ order_id (FK)    │
             │ product_name     │
             │ quantity         │
             │ price            │
             │ subtotal         │
             └──────────────────┘
```

---

## Data Flow

### User Creation Flow
```
1. POST /api/v1/users (User Service)
   ↓
2. Save to PostgreSQL (userdb)
   ↓
3. @Async: Save audit log to MySQL (userdb_secondary)
   ↓
4. Publish USER_CREATED event to Kafka
   ↓
5. Return response to client
```

### Order Creation Flow
```
1. POST /api/v1/orders (Order Service)
   ↓
2. Call User Service via WebClient (with circuit breaker)
   ↓
3. If user valid: Save to PostgreSQL (orderdb)
   ↓
4. @Async: Process order asynchronously
   ↓
5. Publish ORDER_CREATED event to Kafka
   ↓
6. Send notification via RabbitMQ
   ↓
7. Return response to client
```

### Caching Flow
```
1. GET /api/v1/users/1
   ↓
2. Check Redis cache for "users::1"
   ↓
   ├─ CACHE HIT → Return from cache
   │
   └─ CACHE MISS:
      ├→ Query PostgreSQL
      ├→ Store result in Redis (TTL: 10 min)
      └→ Return result
```

---

## Indexing Strategy

### User Service (PostgreSQL)
```sql
-- For fast lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- For filtering
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_role ON users(role);

-- For sorting
CREATE INDEX idx_users_created_at ON users(created_at);

-- Composite index for common queries
CREATE INDEX idx_users_active_role ON users(active, role);
```

### Order Service (PostgreSQL)
```sql
-- For fast lookups
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- For filtering
CREATE INDEX idx_orders_status ON orders(status);

-- For sorting
CREATE INDEX idx_orders_order_date ON orders(order_date DESC);

-- Composite indexes
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_status_date ON orders(status, order_date);
```

### Audit (MySQL)
```sql
-- For lookups by user
CREATE INDEX idx_user_audit_user_id ON user_audit(user_id);

-- For filtering by action
CREATE INDEX idx_user_audit_action ON user_audit(action);

-- For time-based queries
CREATE INDEX idx_user_audit_performed_at ON user_audit(performed_at);

-- Composite
CREATE INDEX idx_user_audit_user_action ON user_audit(user_id, action);
```

---

## Query Examples

### Get user with details
```sql
-- PostgreSQL
SELECT * FROM users WHERE id = 1;
-- Or via cache: Redis -> returns cached User object
```

### Get user's orders
```sql
-- PostgreSQL
SELECT * FROM orders WHERE user_id = 1 ORDER BY order_date DESC;
```

### Get order with items
```sql
-- PostgreSQL
SELECT o.*, 
       array_agg(
           json_build_object(
               'product', oi.product_name,
               'quantity', oi.quantity,
               'price', oi.price
           )
       ) as items
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
WHERE o.id = 1
GROUP BY o.id;
```

### User audit log
```sql
-- MySQL
SELECT * FROM user_audit WHERE user_id = 1 ORDER BY performed_at DESC;
```

### Cache statistics
```bash
# Redis
INFO stats
# Shows hits, misses, etc.
```

---

**All schemas are automatically created/updated by Hibernate (ddl-auto=update)**

**Use these schemas to understand data relationships and improve query performance!**

