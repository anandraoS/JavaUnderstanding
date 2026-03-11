# Practical Docker Implementation Guide

## Part 1: Setting Up Your Spring Boot Application for Docker

### Step 1: Update Your pom.xml

Ensure these dependencies are included:

```xml
<dependencies>
    <!-- Spring Boot Starter Web (includes Tomcat) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot JPA (for database operations) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL JDBC Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Actuator for health checks -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>

<!-- Build configuration -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <!-- Enable layered JAR for Docker optimization -->
                <layers>
                    <enabled>true</enabled>
                </layers>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Step 2: Create Application Configuration Files

**src/main/resources/application.yaml** (Development):
```yaml
spring:
  application:
    name: docker-example-app
  
  # Database configuration
  datasource:
    url: jdbc:mysql://localhost:3306/devdb
    username: dev
    password: dev
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # JPA configuration
  jpa:
    hibernate:
      ddl-auto: create-drop  # Auto-create schema
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: true
  
  # Logging
logging:
  level:
    root: INFO
    com.docker.example: DEBUG
    org.hibernate.SQL: DEBUG

server:
  port: 8080
  servlet:
    context-path: /
```

**src/main/resources/application-docker.yaml** (Docker/Production):
```yaml
spring:
  application:
    name: docker-example-app
  
  datasource:
    # Note: 'mysql' is the service name in Docker network
    # Docker DNS will resolve it to the MySQL container's IP
    url: jdbc:mysql://mysql:3306/applicationdb
    username: appuser
    password: apppassword123
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    # Connection pool settings (important for production)
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate  # IMPORTANT: Don't auto-create in production!
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  
  # Connection to backend from frontend
  cors:
    allowed-origins: "*"
    allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
    allowed-headers: "*"
    allow-credentials: true

logging:
  level:
    root: WARN
    com.docker.example: INFO
  file:
    name: /app/logs/application.log
    max-size: 10MB
    max-history: 10

server:
  port: 8080
  servlet:
    context-path: /
  shutdown: graceful
  tomcat:
    threads:
      max: 200
      min-spare: 10

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Step 3: Create a Sample Controller

**src/main/java/com/docker/example/controller/HelloController.java**:
```java
package com.docker.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Docker!");
    }

    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        return ResponseEntity.ok(
            new HealthStatus("UP", "Application is running")
        );
    }

    @PostMapping("/echo")
    public ResponseEntity<String> echo(@RequestBody String message) {
        return ResponseEntity.ok("Echo: " + message);
    }

    public static class HealthStatus {
        public String status;
        public String message;

        public HealthStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
```

---

## Part 2: Build and Deploy Locally

### Step 1: Build the Project

```bash
# Navigate to project directory
cd C:\Users\sahukari.rao\javaUnderstanding\example

# Build with Maven (creates JAR file)
mvn clean package -DskipTests

# Result: target/example-0.0.1-SNAPSHOT.jar (should be ~50-100MB)
```

### Step 2: Build Docker Image

```bash
# Build image
docker build -t myapp:v1.0 .

# Output:
# Step 1/8 : FROM openjdk:17-slim
#  ---> abc1234567890
# Step 2/8 : WORKDIR /app
#  ---> def0987654321
# ...
# Successfully built abc1234567890
# Successfully tagged myapp:v1.0

# Verify image was created
docker images | grep myapp
# OUTPUT: myapp           v1.0              abc1234567890   2 days ago      800MB
```

### Step 3: Run the Container Standalone (for testing)

```bash
# Run container in background with name 'myapp'
docker run -d \
  --name myapp \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  myapp:v1.0

# Check if running
docker ps
# OUTPUT:
# CONTAINER ID   IMAGE        COMMAND      CREATED      STATUS        PORTS
# a1b2c3d4e5f6   myapp:v1.0   "java -jar"  5 seconds ago Up 4 seconds  0.0.0.0:8080->8080/tcp

# View logs
docker logs -f myapp

# Test the application
curl http://localhost:8080/api/hello
# Response: Hello from Docker!

# Stop the container
docker stop myapp

# Remove the container
docker rm myapp
```

---

## Part 3: Multi-Container Setup with Docker Compose

### Step 1: Prepare MySQL Initialization Script (Optional)

Create **init-db.sql**:
```sql
-- Create application database
CREATE DATABASE IF NOT EXISTS applicationdb;

-- Use the database
USE applicationdb;

-- Create sample table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO users (name, email) VALUES
('John Doe', 'john@example.com'),
('Jane Smith', 'jane@example.com'),
('Bob Johnson', 'bob@example.com');

-- Create index for faster queries
CREATE INDEX idx_email ON users(email);
```

### Step 2: Launch Full Stack with Docker Compose

```bash
# Navigate to project directory
cd C:\Users\sahukari.rao\javaUnderstanding\example

# Start all services
docker-compose up -d

# Output:
# [+] Running 4/4
#  ✔ Network example_app_network Created
#  ✔ Container mysql-db Created
#  ✔ Container spring-backend Created
#  ✔ Container angular-frontend Created
```

### Step 3: Verify Services Are Running

```bash
# List all running services
docker-compose ps

# Output:
# NAME              COMMAND                  SERVICE    STATUS     PORTS
# mysql-db          "docker-entrypoint.s…"   mysql      Up 30s     0.0.0.0:3306->3306/tcp
# spring-backend    "java -jar app.jar"      backend    Up 15s     0.0.0.0:8080->8080/tcp
# angular-frontend  "nginx -g daemon off;"   frontend   Up 10s     0.0.0.0:80->80/tcp

# Check container logs
docker-compose logs backend

# Follow backend logs in real-time
docker-compose logs -f backend

# Check database logs
docker-compose logs mysql

# Check frontend logs
docker-compose logs frontend
```

### Step 4: Test Service Communication

```bash
# Test backend from host
curl http://localhost:8080/api/hello
# Response: Hello from Docker!

# Test health check
curl http://localhost:8080/health
# Response: {"status":"UP","message":"Application is running"}

# Test frontend from host
curl http://localhost/
# Response: HTML from Angular app

# Test inter-container communication
# Execute command inside backend container to test MySQL connection
docker-compose exec backend curl http://mysql:3306 -v
# If successful, MySQL is reachable from backend

# Test backend to MySQL communication
docker-compose exec backend bash -c "mysql -h mysql -u appuser -papppassword123 -e 'SELECT * FROM applicationdb.users;'"
# Shows the users table from MySQL
```

---

## Part 4: Troubleshooting Common Issues

### Issue 1: "Connection refused" when backend tries to reach MySQL

**Symptom:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Causes & Solutions:**

```bash
# 1. Check if MySQL container is running
docker-compose ps
# If mysql shows "Exited", check logs:
docker-compose logs mysql

# 2. Check MySQL health
docker-compose exec mysql mysqladmin ping -h localhost -uroot -prootpassword123
# Should respond: mysqld is alive

# 3. Test connectivity from backend to MySQL
docker-compose exec backend ping mysql
# Should show replies (proves network connectivity)

# 4. Check application configuration
docker-compose exec backend cat application.properties | grep datasource
# Verify: datasource.url=jdbc:mysql://mysql:3306/applicationdb

# 5. Restart services in correct order
docker-compose restart
# or
docker-compose down -v
docker-compose up -d
```

### Issue 2: Frontend cannot reach backend API

**Symptom:**
```
GET http://localhost:8080/api/data - Network Error
CORS blocked error in browser console
```

**Causes & Solutions:**

```bash
# 1. Check backend is running
docker-compose ps backend
# Should show status "Up"

# 2. Check backend logs for errors
docker-compose logs backend | tail -50

# 3. Test backend API directly
curl http://localhost:8080/api/hello -v

# 4. Check nginx configuration
docker-compose exec frontend cat /etc/nginx/nginx.conf | grep -A 5 "proxy_pass"
# Should show: proxy_pass http://backend:8080;

# 5. Test nginx proxy from inside frontend container
docker-compose exec frontend curl http://backend:8080/api/hello

# 6. Check CORS headers
curl http://localhost:8080/api/hello -H "Origin: http://localhost" -v
# Should see: Access-Control-Allow-Origin: *
```

### Issue 3: Port already in use

**Symptom:**
```
Error: Port 8080 is already allocated
Error: Port 80 is already allocated
Error: Port 3306 is already allocated
```

**Solutions:**

```bash
# 1. Find which process is using the port
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # macOS/Linux

# 2. Stop conflicting service
docker stop <container-id>

# 3. Change port in docker-compose.yml
# From:  ports: "8080:8080"
# To:    ports: "8081:8080"  # Now use http://localhost:8081

# 4. Remove all Docker containers and try again
docker-compose down -v
docker-compose up -d
```

### Issue 4: Application keeps restarting

**Symptom:**
```
docker-compose ps shows: backend    Restarting (1) 5 seconds ago
```

**Solutions:**

```bash
# 1. Check detailed logs
docker-compose logs backend

# 2. Look for Java heap space errors
docker-compose logs backend | grep -i "heap\|memory\|outofmemory"

# 3. Increase memory limit in docker-compose.yml
# In backend service:
deploy:
  resources:
    limits:
      memory: 1G  # Increase from 512M

# 4. Check for database connection errors
docker-compose logs backend | grep -i "connection\|refused\|timeout"

# 5. Verify environment variables are set
docker-compose exec backend printenv | grep SPRING_

# 6. Restart with fresh database
docker-compose down -v
docker-compose up -d
```

### Issue 5: MySQL data not persisting

**Symptom:**
```
After docker-compose down, all data is lost
```

**Solution:**

```bash
# 1. Check if volume is created
docker volume ls | grep mysql
# Should show: example_mysql_data

# 2. Verify docker-compose.yml has volumes section
# Should include:
# volumes:
#   mysql_data:
#     driver: local

# 3. Verify services use the volume
# mysql service should have:
# volumes:
#   - mysql_data:/var/lib/mysql

# 4. Check actual volume location
docker volume inspect example_mysql_data
# Shows the Mountpoint on host system

# 5. To backup database
docker-compose exec mysql mysqldump -u appuser -papppassword123 applicationdb > backup.sql

# 6. To restore database
docker-compose exec -T mysql mysql -u appuser -papppassword123 applicationdb < backup.sql
```

---

## Part 5: Deployment Best Practices

### 1. Security Checklist

```yaml
# ✓ Run as non-root user (already in Dockerfile)
USER appuser

# ✓ Use specific tag versions (not 'latest')
FROM openjdk:17-slim  # Specific version, not 'latest'

# ✓ Don't store secrets in environment variables
# Use Docker secrets or external secret management:
# - AWS Secrets Manager
# - HashiCorp Vault
# - Docker Secrets (for Swarm)

# ✓ Use read-only filesystem where possible
# docker run --read-only --tmpfs /app/logs

# ✓ Limit capabilities
# docker run --cap-drop=ALL --cap-add=NET_BIND_SERVICE

# ✓ Enable security options
# docker run --security-opt=no-new-privileges:true
```

### 2. Performance Optimization

```dockerfile
# Use multi-stage build (already done in our Dockerfile)
# This reduces final image size from 800MB to minimal size

# ✓ Cache layers properly
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml .  # Cached if pom.xml hasn't changed
RUN mvn dependency:resolve
COPY src ./src  # Only invalidates cache if src changed
RUN mvn clean package
```

### 3. Monitoring and Logging

```bash
# 1. View real-time logs with timestamps
docker-compose logs -f --timestamps backend

# 2. View last 100 lines
docker-compose logs --tail=100 backend

# 3. Monitor resource usage
docker stats

# 4. Setup log rotation (prevent logs from consuming disk)
# In docker-compose.yml:
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"

# 5. Export logs to external system (Splunk, DataDog, ELK)
# Configure logging drivers in docker-compose.yml
```

### 4. Health Checks

```yaml
# Already configured in docker-compose.yml
# MySQL:
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 40s

# Spring Boot:
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
  interval: 30s
  timeout: 5s
  retries: 3
  start_period: 30s

# Frontend:
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/"]
  interval: 30s
  timeout: 3s
  retries: 3
  start_period: 10s
```

---

## Part 6: Advanced Topics

### 1. Zero-Downtime Deployment

```bash
# Using Docker Compose:
# 1. Update image version
# 2. Run blue-green deployment:

# Current running: docker-compose.yml (blue)
docker-compose up -d

# Create new version: docker-compose.green.yml
# Start green environment:
docker-compose -f docker-compose.green.yml up -d

# Switch load balancer from blue to green
# Then stop blue:
docker-compose down
```

### 2. Scaling Services

```bash
# Scale backend to 3 instances
docker-compose up -d --scale backend=3

# Scale frontend to 2 instances
docker-compose up -d --scale frontend=2

# Requires load balancer to distribute traffic
```

### 3. Using Private Registry

```bash
# Login to private registry
docker login myregistry.company.com

# Tag image for private registry
docker tag myapp:v1.0 myregistry.company.com/myapp:v1.0

# Push to private registry
docker push myregistry.company.com/myapp:v1.0

# Pull from private registry
docker pull myregistry.company.com/myapp:v1.0

# Use in docker-compose.yml:
services:
  backend:
    image: myregistry.company.com/myapp:v1.0
```

---

## Part 7: Complete Workflow

```bash
# 1. DEVELOPMENT
# Create code, test locally
mvn clean package
docker build -t myapp:dev .
docker run -it myapp:dev

# 2. TESTING
# Run full stack locally
docker-compose up -d
# Run integration tests
docker-compose exec backend mvn test

# 3. STAGING
# Build final image
docker build -t myapp:v1.0 .
# Push to registry
docker push myregistry.com/myapp:v1.0

# 4. PRODUCTION
# Update docker-compose production config
docker-compose -f docker-compose.prod.yml up -d
# Monitor
docker-compose logs -f

# 5. MAINTENANCE
# View logs
docker-compose logs backend
# Scale if needed
docker-compose up -d --scale backend=5
# Update services
docker-compose pull
docker-compose up -d
```

---

This practical guide helps you deploy and manage your Docker applications from development to production!

