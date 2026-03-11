# Docker Quick Start Guide for Your Project

## Overview

This directory contains a complete Spring Boot + Angular + MySQL application containerized with Docker.

## Project Structure

```
example/
├── DOCKER_GUIDE.md              # Comprehensive Docker documentation
├── DOCKER_QUICKSTART.md         # This file - Quick start instructions
├── Dockerfile                   # Spring Boot container definition
├── Dockerfile.angular           # Angular container definition
├── docker-compose.yml           # Production-like configuration
├── docker-compose.dev.yml       # Development configuration
├── nginx.conf                   # Nginx configuration for Angular
├── src/                         # Spring Boot source code
│   ├── main/
│   │   ├── java/
│   │   │   └── com/docker/example/
│   │   │       └── ExampleApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-docker.yaml
│   └── test/
├── frontend/                    # Angular application (not included, add separately)
│   ├── src/
│   ├── package.json
│   └── Dockerfile.angular
└── pom.xml                      # Maven configuration

```

## Prerequisites

Install the following:
1. **Docker** - https://www.docker.com/products/docker-desktop
2. **Docker Compose** - Usually included with Docker Desktop
3. **Git** (optional) - For version control
4. **Maven** (optional) - For local builds without Docker

Verify installation:
```bash
docker --version
docker-compose --version
```

## Quick Start (Production Setup)

### Step 1: Build and Run Everything with One Command

```bash
# Navigate to project directory
cd C:\Users\sahukari.rao\javaUnderstanding\example

# Start all services (MySQL, Spring Boot, Angular)
docker-compose up -d

# View logs
docker-compose logs -f
```

**What happens:**
1. Docker creates `app_network` bridge network
2. Pulls/builds MySQL image → starts mysql-db container
3. Builds Spring Boot image → starts spring-backend container
4. Builds Angular image → starts angular-frontend container
5. All containers connect to same network
6. Spring Boot waits for MySQL health check before starting
7. Angular starts and proxies requests to Spring Boot

### Step 2: Access Your Application

- **Frontend**: http://localhost (Angular running on Nginx)
- **Backend API**: http://localhost:8080 (Spring Boot)
- **Database**: localhost:3306 (MySQL)

### Step 3: Check Services

```bash
# List running containers
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Execute command in container
docker-compose exec backend bash

# Check container resource usage
docker stats
```

### Step 4: Stop Everything

```bash
# Stop and remove containers (keeps volumes)
docker-compose down

# Stop and remove everything including volumes (clears database)
docker-compose down -v
```

---

## Development Setup

Use this for active development with hot-reload:

```bash
# Start development environment
docker-compose -f docker-compose.dev.yml up -d

# Angular dev server: http://localhost:4200
# Spring Boot: http://localhost:8080
# MySQL: localhost:3306
```

**Features:**
- Angular hot-reload: Changes in code auto-refresh browser
- Spring Boot auto-reload: Changes in Java code auto-restart
- Remote debugging: Debug Spring Boot from IDE (port 5005)

---

## Understanding the Architecture

### Container Network Communication

```
┌──────────────────────────────────────────────────────────┐
│               Docker Bridge Network                       │
│               (app_network)                              │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────┐  │
│  │   Frontend   │   │   Backend    │   │   MySQL    │  │
│  │  (Nginx:80)  │   │ (Java:8080)  │   │ (Port:3306)│  │
│  │              │   │              │   │            │  │
│  │  Service     │   │  Service     │   │  Service   │  │
│  │  name:       │   │  name:       │   │  name:     │  │
│  │  "frontend"  │   │  "backend"   │   │  "mysql"   │  │
│  └──────────────┘   └──────────────┘   └────────────┘  │
│        │                   │                   │        │
│        └───────────────────┼───────────────────┘        │
│                            │                            │
│        Docker DNS Server (127.0.0.11:53)                │
│        Routes service names to IP addresses             │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Data Flow

**1. Client Browser → Frontend**
```
HTTP GET http://localhost/
        ↓ (Port binding: Host:80 → Container:80)
Nginx Container receives request
        ↓
Serves Angular static files
        ↓
Returns HTML, CSS, JavaScript to Browser
```

**2. Frontend → Backend API**
```
fetch('http://backend:8080/api/data')
        ↓ (DNS resolution in container)
Docker resolves "backend" → 172.18.0.3:8080
        ↓
Nginx proxies request to Spring Boot container
        ↓
Spring Boot processes request
        ↓
Returns JSON response to Frontend
```

**3. Backend → Database**
```
DataSource.getConnection(
  "jdbc:mysql://mysql:3306/applicationdb"
)
        ↓ (DNS resolution in container)
Docker resolves "mysql" → 172.18.0.4:3306
        ↓
JDBC driver connects to MySQL container
        ↓
Executes SQL query
        ↓
Returns results to Spring Boot
```

---

## Troubleshooting

### Issue: "Connection refused" from Frontend to Backend

**Cause**: Containers not on same network or backend not started

**Solution**:
```bash
# Check if both containers are running
docker-compose ps

# Check network connections
docker-compose exec frontend ping backend

# Check backend logs
docker-compose logs backend
```

### Issue: "Database connection failed"

**Cause**: MySQL not ready when Spring Boot starts

**Solution**: Verify health check passed:
```bash
docker-compose logs mysql

# Force restart backend after MySQL is ready
docker-compose restart backend
```

### Issue: Changes in code not reflecting

**Cause**: Using production docker-compose instead of dev

**Solution**:
```bash
# Use development compose file
docker-compose -f docker-compose.dev.yml up -d
```

### Issue: Port already in use

**Cause**: Another service using port 80, 8080, or 3306

**Solution**:
```bash
# Change port in docker-compose.yml
ports:
  - "8081:8080"  # Changed from 8080:8080
```

---

## Common Commands

```bash
# Build images
docker-compose build

# Build specific service
docker-compose build backend

# View logs (last 50 lines)
docker-compose logs --tail=50

# Real-time logs
docker-compose logs -f backend

# Execute command
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p

# Check resource usage
docker stats

# Inspect network
docker network inspect example_app_network

# Remove containers and volumes
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build

# View image size
docker images
```

---

## Environment Variables

### Spring Boot (In Container)

Modify in `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/applicationdb
  SPRING_DATASOURCE_USERNAME: appuser
  SPRING_DATASOURCE_PASSWORD: apppassword123
  SPRING_JPA_HIBERNATE_DDL_AUTO: validate
  JAVA_OPTS: -Xmx512m -Xms256m
```

### MySQL (In Container)

```yaml
environment:
  MYSQL_ROOT_PASSWORD: rootpassword123
  MYSQL_DATABASE: applicationdb
  MYSQL_USER: appuser
  MYSQL_PASSWORD: apppassword123
```

---

## Production Deployment

For production, follow these best practices:

1. **Use production-grade configurations**
   - Set `SPRING_JPA_HIBERNATE_DDL_AUTO: validate` (don't auto-create)
   - Reduce memory limits to save costs
   - Add resource reservations

2. **Security**
   - Use environment files instead of hardcoding passwords
   - Run containers as non-root user
   - Use read-only filesystems where possible
   - Scan images for vulnerabilities

3. **Monitoring**
   - Enable health checks
   - Set up logging aggregation
   - Monitor resource usage
   - Track application metrics

4. **Deployment**
   - Use Kubernetes or Docker Swarm for orchestration
   - Use private container registry
   - Implement CI/CD pipeline
   - Use rolling updates for zero-downtime deployment

---

## Building Images Manually

If you want to build images without docker-compose:

```bash
# Build Spring Boot image
docker build -t my-spring-app:v1.0 .

# Build Angular image
docker build -t my-angular-app:v1.0 -f Dockerfile.angular ./frontend

# Create network
docker network create myapp-network

# Run MySQL
docker run -d \
  --name mysql \
  --network myapp-network \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=mydb \
  mysql:8.0

# Run Spring Boot
docker run -d \
  --name backend \
  --network myapp-network \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/mydb \
  my-spring-app:v1.0

# Run Angular
docker run -d \
  --name frontend \
  --network myapp-network \
  -p 80:80 \
  my-angular-app:v1.0
```

---

## Next Steps

1. **Read DOCKER_GUIDE.md** for comprehensive Docker concepts
2. **Create your Angular application** and add to `frontend/` directory
3. **Add API controller** to Spring Boot for data endpoints
4. **Configure MySQL schema** with init script
5. **Set up CI/CD** pipeline for automated builds
6. **Deploy to cloud** (AWS, Azure, Google Cloud)

---

## Resources

- [Docker Official Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Angular Docker Best Practices](https://angular.io/guide/docker)

---

## Support

For issues or questions:
1. Check DOCKER_GUIDE.md for detailed explanations
2. Review docker-compose logs: `docker-compose logs`
3. Check Docker documentation
4. Use `docker-compose down -v` to reset everything and start fresh

