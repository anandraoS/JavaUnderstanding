# Comprehensive Docker Guide: Architecture, Deployment & Inter-Container Communication

## Table of Contents
1. [Docker Fundamentals](#docker-fundamentals)
2. [Docker Architecture](#docker-architecture)
3. [Core Concepts](#core-concepts)
4. [Deploying Spring Boot Applications](#deploying-spring-boot-applications)
5. [Deploying Angular Applications](#deploying-angular-applications)
6. [Inter-Container Communication](#inter-container-communication)
7. [Practical Examples](#practical-examples)
8. [Advanced Concepts](#advanced-concepts)

---

## Docker Fundamentals

### What is Docker?

Docker is a **containerization platform** that packages your entire application (code, runtime, libraries, dependencies) into a lightweight, portable unit called a **container**.

### Why Docker?

```
Traditional Deployment          Docker Deployment
┌─────────────────────┐        ┌──────────────────┐
│  Application Code   │        │  Application Code│
│  Dependencies       │        │  Dependencies    │
│  Java Runtime       │        │  Java Runtime    │
│  OS Libraries       │        │  Minimal OS      │
│  Full OS (GBs)      │        │  Container (MBs) │
│  Complex Setup      │        │  Easy Setup      │
└─────────────────────┘        └──────────────────┘
  "It works on my machine"      Works everywhere!
  Problem: Dependencies vary    Consistency across environments
           OS conflicts         Faster deployment
           Version mismatch     Easy scaling
```

### Container vs Virtual Machine

```
┌─────────────────────────────────────────────────────────────┐
│                    Traditional VMs                          │
├─────────────────────────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│ │Guest OS  │  │Guest OS  │  │Guest OS  │                   │
│ │(500MB)   │  │(500MB)   │  │(500MB)   │                   │
│ └──────────┘  └──────────┘  └──────────┘                   │
│ ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│ │ App1     │  │ App2     │  │ App3     │                   │
│ └──────────┘  └──────────┘  └──────────┘                   │
│ ┌─────────────────────────────────────────────────────────┐│
│ │         Hypervisor (Virtual Machine Manager)           ││
│ └─────────────────────────────────────────────────────────┘│
│ ┌─────────────────────────────────────────────────────────┐│
│ │                    Host OS                             ││
│ └─────────────────────────────────────────────────────────┘│
│ ┌─────────────────────────────────────────────────────────┐│
│ │                  Hardware                              ││
│ └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
- Each VM requires full OS installation (slow, heavy)
- Boot time: 1-2 minutes
- Resource overhead: Each OS takes 500MB-1GB+ RAM


┌─────────────────────────────────────────────────────────────┐
│                     Docker Containers                       │
├─────────────────────────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│ │ App1     │  │ App2     │  │ App3     │                   │
│ │Lightweight   Lightweight   Lightweight│                   │
│ │(100MB)   │  │(100MB)   │  │(100MB)   │                   │
│ └──────────┘  └──────────┘  └──────────┘                   │
│ ┌────────────────────────────────────────────────────────┐  │
│ │          Docker Engine (Container Runtime)             │  │
│ └────────────────────────────────────────────────────────┘  │
│ ┌────────────────────────────────────────────────────────┐  │
│ │                    Host OS                            │  │
│ └────────────────────────────────────────────────────────┘  │
│ ┌────────────────────────────────────────────────────────┐  │
│ │                  Hardware                             │  │
│ └────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
- Shared host OS (efficient)
- Boot time: Milliseconds
- Resource overhead: Minimal (shared kernel)
```

---

## Docker Architecture

### Docker Internal Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                   Docker Architecture                        │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Docker Client (CLI)                       │ │
│  │  $ docker run, docker build, docker ps, etc.          │ │
│  └────────────────────────────────────────────────────────┘ │
│                           ▲                                  │
│                           │ REST API                         │
│                           ▼                                  │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Docker Daemon (dockerd)                   │ │
│  │  - Manages containers                                 │ │
│  │  - Manages images                                     │ │
│  │  - Manages networks                                   │ │
│  │  - Manages volumes                                    │ │
│  └────────────────────────────────────────────────────────┘ │
│                           │                                  │
│        ┌──────────────────┼──────────────────┐              │
│        ▼                  ▼                  ▼              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    Image     │  │  Container   │  │   Storage    │     │
│  │  Registry    │  │  Runtime     │  │   (Volumes)  │     │
│  │              │  │  (containerd)│  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Key Components:

1. **Docker Client**: Command-line tool you use (`docker` commands)
2. **Docker Daemon**: Background service managing containers
3. **Container Runtime**: Runs the actual containers (containerd)
4. **Image Registry**: Stores and distributes Docker images (Docker Hub, etc.)
5. **Storage Driver**: Manages filesystem layers

---

## Core Concepts

### 1. Docker Image

A **Docker Image** is a **blueprint** or **template** for creating containers.

```
What is an Image?
─────────────────────────────────────────────────────────────

Image Structure (Layered):
┌─────────────────────────────────────┐
│  Layer 5: Your Application Code     │ (Read-Only)
├─────────────────────────────────────┤
│  Layer 4: Dependencies (npm, Maven) │ (Read-Only)
├─────────────────────────────────────┤
│  Layer 3: Runtime (Java, Node.js)   │ (Read-Only)
├─────────────────────────────────────┤
│  Layer 2: Base OS (Alpine, Ubuntu)  │ (Read-Only)
├─────────────────────────────────────┤
│  Layer 1: Kernel Libraries          │ (Read-Only)
└─────────────────────────────────────┘

How Layering Works:
- Each instruction in Dockerfile creates a NEW LAYER
- Layers are cached for faster rebuilds
- Only changed layers are rebuilt
- Same base layers shared by multiple images (saves space)

Example Image Size:
┌──────────────────────────────────────┐
│  My Spring Boot Image (1.2GB)        │
├──────────────────────────────────────┤
│  Layer: Spring Boot deps (500MB)     │
│  Layer: Java Runtime (800MB)         │
│  Layer: Ubuntu Base (200MB)          │
└──────────────────────────────────────┘

When you run image → creates Container (Read-Write layer on top)
```

### 2. Docker Container

A **Docker Container** is a **running instance** of a Docker image.

```
Image vs Container Analogy:
────────────────────────────────────────
Image  = Class (Blueprint)
        = Recipe for cake

Container = Object Instance (Running Process)
          = Actual cake

Multiple containers can run from same image!

┌─────────────────┐
│  Docker Image   │
│  (Blueprint)    │
└────────┬────────┘
         │
    ┌────┴───┬─────────┬──────────┐
    ▼        ▼         ▼          ▼
┌────────┐┌────────┐┌────────┐┌────────┐
│ Cont.1 ││ Cont.2 ││ Cont.3 ││ Cont.4 │
│Running ││Running ││Stopped ││Running │
└────────┘└────────┘└────────┘└────────┘

Each container has isolated:
- Filesystem
- Process space (PID namespace)
- Network (IP, ports)
- Environment variables
```

### 3. Dockerfile

A **Dockerfile** is a text file containing instructions to **build** a Docker image.

```dockerfile
# Dockerfile is like a recipe
Instruction -> Action -> Layer Created

FROM ubuntu:20.04          # Base image (start here)
                           # ↓ Layer 1

RUN apt-get update && \    # Execute command (install packages)
    apt-get install -y \
    openjdk-17-jdk
                           # ↓ Layer 2

WORKDIR /app               # Set working directory
                           # ↓ Layer 3

COPY app.jar app.jar       # Copy files from host to image
                           # ↓ Layer 4

ENV JAVA_OPTS="-Xmx512m"   # Set environment variables
                           # ↓ Layer 5

EXPOSE 8080                # Document exposed port
                           # ↓ Layer 6

CMD ["java", "-jar",       # Default command to run
     "app.jar"]            # ↓ Layer 7
```

### 4. Registry and Repositories

```
Docker Registry Architecture:
─────────────────────────────────────────────────────────────

Public Registry (Docker Hub)
┌─────────────────────────────────────────────────────────┐
│  docker.io/library/ubuntu      (Official)               │
│  docker.io/library/nginx                                │
│  docker.io/library/postgres                             │
│  docker.io/library/openjdk                              │
│                                                         │
│  docker.io/username/myapp      (User repositories)      │
│  docker.io/company/service                              │
└─────────────────────────────────────────────────────────┘

Private Registry
┌─────────────────────────────────────────────────────────┐
│  registry.company.com/myapp:v1.0                        │
│  registry.company.com/api-service:latest               │
│  registry.company.com/frontend:prod                    │
└─────────────────────────────────────────────────────────┘

Image Naming Convention:
─────────────────────────────────────────────────────────────
[REGISTRY_URL]/[REPOSITORY]/[IMAGE_NAME]:[TAG]

Examples:
- ubuntu:20.04
- openjdk:17-slim
- myregistry.com/myapp:v1.2.3
- docker.io/library/postgres:latest
- gcr.io/myproject/service:prod
```

---

## Deploying Spring Boot Applications

### Spring Boot in Docker - Complete Workflow

```
Development              Build                Runtime
┌──────────────┐    ┌────────────────┐    ┌────────────────┐
│ Source Code  │    │  Dockerfile    │    │  Registry      │
│ (Java)       │───→│  + Build Tools │───→│  (Docker Hub)  │
│              │    │                │    │                │
└──────────────┘    └────────────────┘    └────────────────┘
                           │
                           │ docker build
                           ▼
                    ┌────────────────┐
                    │  Docker Image  │
                    │  (Layers)      │
                    └────────────────┘
                           │
                           │ docker push
                           ▼
                    ┌────────────────┐
                    │   Registry     │
                    └────────────────┘
                           │
                           │ docker pull
                           ▼
                    ┌────────────────┐
                    │  Production    │
                    │  Server        │
                    └────────────────┘
                           │
                           │ docker run
                           ▼
                    ┌────────────────┐
                    │  Container     │
                    │  Running App   │
                    └────────────────┘
```

### Step-by-Step: Containerizing Spring Boot

#### Step 1: Create Dockerfile

```dockerfile
# Use OpenJDK 17 slim image as base (smaller than full image)
FROM openjdk:17-slim

# Set working directory inside container
WORKDIR /app

# Copy built JAR file from host to container
COPY target/example-0.0.1-SNAPSHOT.jar app.jar

# Expose port (documentation, not binding)
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check (optional, but recommended)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher --health

# Command to run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
```

#### Step 2: Build the Image

```bash
# Command
docker build -t myapp:v1.0 .

# What happens internally
├─ Reads Dockerfile
├─ Executes each instruction sequentially
├─ Creates a layer for each instruction
├─ Caches layers for faster rebuilds
└─ Tags final image as "myapp:v1.0"

# Build Output
Step 1/6 : FROM openjdk:17-slim
 ---> abc1234567890  (Layer hash)
Step 2/6 : WORKDIR /app
 ---> Running in temp_container
 ---> def0987654321
Step 3/6 : COPY target/example-0.0.1-SNAPSHOT.jar app.jar
 ---> ghi1234567890
...
Successfully built ghi1234567890
Successfully tagged myapp:v1.0
```

#### Step 3: Run the Container

```bash
# Command
docker run -d \
  --name myapp-container \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -v /app/logs:/var/log/app \
  myapp:v1.0

# What happens internally
docker run
  ├─ Pull image if not exists
  ├─ Create container from image
  │  ├─ Create filesystem layer (writable)
  │  ├─ Set up networking namespace
  │  ├─ Set up process namespace
  │  └─ Set up filesystem mounts
  ├─ Start container
  │  ├─ Execute ENTRYPOINT
  │  ├─ Run Java application
  │  └─ Application listens on port 8080
  └─ Return container ID

# Port Mapping Visualization
Host Machine              Container
Port 8080  ──────────────→ Port 8080 (Application)
           (Binding)

Requests from outside world:
Client → Host:8080 → Docker Bridge Network → Container:8080
```

### Spring Boot Application Properties

```yaml
# application.yaml - Production Configuration
spring:
  application:
    name: my-spring-app
    
  datasource:
    url: jdbc:mysql://mysql-container:3306/mydb
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

server:
  port: 8080
  shutdown: graceful
  
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true

# Notice: mysql-container (service name in Docker network)
# This is how containers communicate internally!
```

---

## Deploying Angular Applications

### Angular in Docker - Complete Workflow

```
Angular Deployment Flow:
─────────────────────────────────────────────────────────────

Multi-Stage Build (Recommended):

Stage 1: Build Stage
┌─────────────────────────────────────┐
│ FROM node:18-alpine                 │
│ WORKDIR /app                        │
│ COPY package*.json ./               │
│ RUN npm install                     │
│ COPY . .                            │
│ RUN npm run build                   │
│                                     │
│ Creates: dist/ folder (optimized)   │
└─────────────────────────────────────┘
           │
           ▼
Stage 2: Runtime Stage
┌─────────────────────────────────────┐
│ FROM nginx:alpine                   │
│ COPY --from=builder /app/dist ./    │
│ EXPOSE 80                           │
│ CMD ["nginx", "-g", "daemon off;"]  │
│                                     │
│ Final image: only runtime + files   │
│ (no build tools included)           │
└─────────────────────────────────────┘

Result:
- Build stage: 800MB (includes node, npm, dependencies)
- Final stage: 50MB (only nginx + compiled files)
```

### Step-by-Step: Containerizing Angular

#### Step 1: Create Dockerfile (Multi-stage)

```dockerfile
# Stage 1: Build Stage
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build Angular app
RUN npm run build

# Stage 2: Runtime Stage
FROM nginx:alpine

# Copy compiled Angular app from builder stage
COPY --from=builder /app/dist/my-angular-app /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 (HTTP)
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

#### Step 2: Create nginx.conf

```nginx
events {
    worker_connections 1024;
}

http {
    # Cache static assets
    gzip on;
    gzip_types text/css application/javascript image/svg+xml;

    upstream api_backend {
        server spring-api:8080;  # Reference to Spring Boot container
    }

    server {
        listen 80;
        server_name _;
        root /usr/share/nginx/html;
        index index.html;

        # Serve static files
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 365d;
            add_header Cache-Control "public, immutable";
        }

        # API proxy to Spring Boot backend
        location /api/ {
            proxy_pass http://api_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Angular routing (SPA)
        location / {
            try_files $uri $uri/ /index.html;
        }
    }
}
```

#### Step 3: Build and Run

```bash
# Build Angular Docker image
docker build -t myapp-frontend:v1.0 .

# Run the container
docker run -d \
  --name myapp-frontend \
  -p 80:80 \
  --network myapp-network \
  myapp-frontend:v1.0

# The network is crucial for inter-container communication!
```

---

## Inter-Container Communication

This is the **KEY CONCEPT** for multi-container applications!

### Docker Networking Architecture

```
┌─────────────────────────────────────────────────────────────┐
│              Docker Networking Overview                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐ │
│  │   Frontend   │    │   Backend    │    │   Database   │ │
│  │  Container   │    │  Container   │    │  Container   │ │
│  │  (Angular)   │    │  (Spring)    │    │  (MySQL)     │ │
│  │  nginx:80    │    │  Java:8080   │    │  MySql:3306  │ │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘ │
│         │                   │                    │         │
│         └───────────────────┼────────────────────┘         │
│                             │                              │
│        ┌────────────────────┴────────────────────┐         │
│        ▼                                         ▼         │
│  ┌──────────────────────────────────────────────────────┐ │
│  │         Docker Bridge Network (myapp-network)       │ │
│  │                                                      │ │
│  │  Each container has internal DNS name:              │ │
│  │  - frontend (resolves to 172.18.0.2)               │ │
│  │  - backend (resolves to 172.18.0.3)                │ │
│  │  - mysql (resolves to 172.18.0.4)                  │ │
│  │                                                      │ │
│  │  Communication: frontend → backend → mysql          │ │
│  │  Instead of: 172.18.0.2 → 172.18.0.3 → 172.18.0.4  │ │
│  └──────────────────────────────────────────────────────┘ │
│                             │                              │
│              ┌──────────────┴──────────────┐               │
│              ▼                             ▼               │
│        ┌──────────────┐            ┌──────────────┐       │
│        │  Host System │            │  Host System │       │
│        │ Network      │            │ Firewall     │       │
│        └──────────────┘            └──────────────┘       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Communication Methods

#### Method 1: Docker Bridge Network (Recommended)

```
Communication Flow:
─────────────────────────────────────────────────────────────

1. Create Network
   $ docker network create myapp-network

2. Run Containers on Network
   $ docker run --network myapp-network --name backend myapp-backend:v1.0
   $ docker run --network myapp-network --name frontend myapp-frontend:v1.0

3. Internal Communication
   
   Frontend Container wants to connect to Backend:
   ┌──────────────────────────────────────────────────┐
   │ Frontend Container                               │
   ├──────────────────────────────────────────────────┤
   │ Code: fetch('http://backend:8080/api/data')      │
   │       ↓                                          │
   │ Docker DNS Resolver (127.0.0.11:53)              │
   │       ↓                                          │
   │ Resolves "backend" → 172.18.0.3                  │
   │       ↓                                          │
   │ Sends request to 172.18.0.3:8080                 │
   │       ↓                                          │
   │ Backend Container receives on port 8080          │
   └──────────────────────────────────────────────────┘

4. How it Works Internally
   
   Docker Embedded DNS (127.0.0.11:53)
   ┌─────────────────────────────────────┐
   │ Service Discovery Database:         │
   │ backend   → 172.18.0.3:8080        │
   │ frontend  → 172.18.0.2:80          │
   │ mysql     → 172.18.0.4:3306        │
   │ redis     → 172.18.0.5:6379        │
   └─────────────────────────────────────┘
   
   When container starts → automatically registered!
   When container stops → automatically deregistered!
```

#### Method 2: Container Linking (Legacy, not recommended)

```bash
# Old way (deprecated)
docker run --name backend myapp-backend:v1.0
docker run --link backend --name frontend myapp-frontend:v1.0

# Problems:
# - Single direction (frontend → backend, not reverse)
# - Not flexible
# - Tightly coupled
# - Doesn't work with swarm/Kubernetes
```

#### Method 3: Localhost & Port Binding (Simple apps)

```bash
# All on localhost (only for single-host testing)
docker run -p 8080:8080 backend     # Backend on host:8080
docker run -p 80:80 frontend        # Frontend on host:80

# Frontend accesses: http://localhost:8080
# But this only works on single host!
```

### Real-World Example: Full Stack Application

```
Architecture:
─────────────────────────────────────────────────────────────

                    ┌─────────────────────────────────────┐
                    │    Client's Browser/Device          │
                    │    (Outside Docker)                 │
                    └──────────────┬──────────────────────┘
                                   │
                                   │ HTTP Request
                                   │ (Port 80)
                                   ▼
         ┌─────────────────────────────────────────────────┐
         │        Host Machine Port Bindings               │
         │                                                 │
         │  Host:80  ──→  Container:80  (Frontend)         │
         │  Host:8080 ──→ Container:8080 (Backend)         │
         │  Host:3306 ──→ Container:3306 (MySQL)           │
         └──────────────────┬──────────────────────────────┘
                            │
         ┌──────────────────┴──────────────────┐
         ▼                                      ▼
    ┌─────────────────┐    ┌──────────────────────────┐
    │ Frontend        │    │ Backend & Database Network │
    │ Container       │    │ (Docker Bridge)           │
    │                 │    │                          │
    │ Nginx:80        │    │ ┌────────────────────┐   │
    │                 │    │ │ API Container      │   │
    │ listens on :80  │    │ │ Spring Boot:8080   │   │
    │                 │    │ │                    │   │
    │ Request comes   │    │ │ Listens on :8080   │   │
    │ from Client     │    │ │                    │   │
    │                 │    │ └──────────┬─────────┘   │
    └────────┬────────┘    │            │             │
             │             │ Service name: "backend"  │
             │             │            │             │
             │ fetch       │ ┌──────────▼─────────┐   │
             │ /api/data   │ │ MySQL Container    │   │
             │             │ │ MySQL:3306         │   │
             │ (to:        │ │                    │   │
             │  backend:   │ │ Listens on :3306   │   │
             │  8080)      │ │                    │   │
             │             │ └────────────────────┘   │
             │             │                          │
             │             │ Service name: "mysql"    │
             └─────────────┼──────────────────────────┘
                           │
                    Internal DNS
                    (127.0.0.11:53)
```

### Communication Code Examples

#### Frontend (Angular) to Backend (Spring)

**frontend/src/app/services/api.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  
  // Using service name instead of IP!
  private apiUrl = 'http://backend:8080/api';
  
  constructor(private http: HttpClient) { }
  
  getData() {
    return this.http.get(`${this.apiUrl}/data`);
  }
  
  postData(data: any) {
    return this.http.post(`${this.apiUrl}/data`, data);
  }
}
```

**How it resolves:**
```
1. Angular app runs in container named "frontend"
2. Makes request to: http://backend:8080/api/data
3. Docker DNS resolver (running in container) intercepts
4. Looks up "backend" in service discovery database
5. Finds: backend → 172.18.0.3
6. Rewrites request to: http://172.18.0.3:8080/api/data
7. Sends to backend container
```

#### Backend (Spring) to Database (MySQL)

**application.yaml (in Spring container):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/mydb    # Service name: mysql
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**How it resolves:**
```
1. Spring application in "backend" container
2. DataSource connects to: jdbc:mysql://mysql:3306/mydb
3. Docker DNS resolver intercepts
4. Looks up "mysql" in service discovery
5. Finds: mysql → 172.18.0.4
6. Rewrites to: jdbc:mysql://172.18.0.4:3306/mydb
7. MySQL driver connects to 172.18.0.4:3306
```

---

## Practical Examples

### Example 1: Simple Spring Boot Container

#### Directory Structure:
```
example/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/docker/example/
│   │   │       ├── ExampleApplication.java
│   │   │       ├── controller/
│   │   │       │   └── HelloController.java
│   │   │       └── model/
│   │   │           └── Message.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-docker.yaml
│   └── test/
│       └── java/...
├── pom.xml
└── Dockerfile
```

#### Dockerfile:
```dockerfile
# Multi-stage build for Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim

WORKDIR /app

COPY --from=builder /build/target/example-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

#### Build & Run:
```bash
# Build image
docker build -t my-spring-app:v1.0 .

# Run container
docker run -d \
  --name spring-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  my-spring-app:v1.0

# Verify
docker logs spring-app
curl http://localhost:8080/health
```

### Example 2: Angular + Spring Boot + MySQL Stack

#### Create Docker Compose file (docker-compose.yml):

```yaml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: mysql-container
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mydb
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10
    networks:
      - myapp-network

  # Spring Boot Backend
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: spring-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mydb
      SPRING_DATASOURCE_USERNAME: appuser
      SPRING_DATASOURCE_PASSWORD: apppassword
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - myapp-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 3s
      retries: 3

  # Angular Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: angular-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - myapp-network
    environment:
      BACKEND_URL: http://backend:8080

volumes:
  mysql-data:
    driver: local

networks:
  myapp-network:
    driver: bridge
```

#### Deploy the entire stack:

```bash
# Start all containers
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop all containers
docker-compose down

# Remove volumes (clears database)
docker-compose down -v
```

---

## Advanced Concepts

### 1. Container Lifecycle

```
Container States:
─────────────────────────────────────────────────────────────

                    docker run
                       │
                       ▼
    ┌──────────────────────────────────────┐
    │         CREATED                      │
    │  Container prepared but not running  │
    └──────────┬───────────────────────────┘
               │
        docker start
               │
               ▼
    ┌──────────────────────────────────────┐
    │         RUNNING                      │
    │  Container is actively executing     │
    │  - Process running (PID 1)           │
    │  - Ports open                        │
    │  - Logs being generated              │
    └──────┬───────────────────┬───────────┘
           │                   │
    pause  │                   │ stop/kill
           │                   │
           ▼                   ▼
    ┌──────────────┐   ┌──────────────────┐
    │  PAUSED      │   │  STOPPED         │
    │  (Suspended) │   │  (Exited)        │
    └──────────────┘   └──────────────────┘

Event Hooks:
─────────────────────────────────────────────────────────────

docker run
   ├─ Create container
   ├─ Create filesystem (Copy from image)
   ├─ Set up networking
   ├─ Start container
   │  ├─ Execute ENTRYPOINT
   │  ├─ Execute CMD
   │  └─ Process becomes PID 1
   ├─ Application starts
   │  ├─ Initialization code runs
   │  ├─ Open files/sockets
   │  └─ Start listening on ports
   │
   ├─ Signal: SIGTERM (graceful shutdown)
   │  ├─ Application receives signal
   │  ├─ Close connections gracefully
   │  ├─ Stop listening
   │  ├─ Wait timeout (default 10s)
   │  └─ Clean up resources
   │
   └─ Signal: SIGKILL (force kill after timeout)
      └─ Process terminated immediately
```

### 2. Storage: Volumes and Mounts

```
Three Types of Storage:
─────────────────────────────────────────────────────────────

Type 1: Volume Mount (Managed by Docker)
┌────────────────────────────────────────────┐
│  Host                      Container       │
│  ┌────────────────────┐   ┌────────────┐  │
│  │ /var/lib/docker/  │   │ /app/data  │  │
│  │   volumes/        │   │            │  │
│  │   myvolume/       │──→│            │  │
│  │   [files]         │   │            │  │
│  └────────────────────┘   └────────────┘  │
└────────────────────────────────────────────┘
- Docker manages location
- Easy backup & portability
- Survives container deletion

Type 2: Bind Mount (Direct host directory)
┌────────────────────────────────────────────┐
│  Host                      Container       │
│  ┌────────────────────┐   ┌────────────┐  │
│  │ /home/user/        │   │ /app/data  │  │
│  │   myappdata/       │   │            │  │
│  │   [files]          │──→│            │  │
│  └────────────────────┘   └────────────┘  │
└────────────────────────────────────────────┘
- Direct access to host filesystem
- Good for development
- Explicit path control

Type 3: Tmpfs Mount (Memory-based)
┌────────────────────────────────────────────┐
│  Container                                 │
│  ┌──────────────────────┐                  │
│  │ /dev/shm/            │                  │
│  │ (In RAM - fast)      │                  │
│  │ (Cleared on restart) │                  │
│  └──────────────────────┘                  │
└────────────────────────────────────────────┘
- Ultra-fast temporary storage
- Lost when container stops
- Good for cache, temp files

Usage Examples:
─────────────────────────────────────────────────────────────

# Named volume (recommended)
docker run -v mydata:/app/data myapp

# Bind mount
docker run -v /host/path:/container/path myapp

# Tmpfs mount
docker run --tmpfs /app/cache myapp

# In Docker Compose
volumes:
  mysql_data:  # Named volume
    driver: local

services:
  db:
    volumes:
      - mysql_data:/var/lib/mysql
      - ./config:/etc/mysql/conf.d  # Bind mount
      - /dev/shm:/shm               # Tmpfs
```

### 3. Resource Management

```
Container Resource Limits:
─────────────────────────────────────────────────────────────

Without Limits:
┌──────────────────────────────────────────┐
│ Container A (No Limits)                  │
│ Uses: 50% CPU, 80% RAM                   │
│ → Starves other containers               │
│                                          │
│ Container B (No Limits)                  │
│ Uses: 30% CPU, 10% RAM                   │
│ → Degraded performance                   │
│                                          │
│ Container C (No Limits)                  │
│ Uses: 20% CPU, 10% RAM                   │
│ → Occasional slowness                    │
└──────────────────────────────────────────┘

With Resource Limits:
┌──────────────────────────────────────────┐
│ Container A (Limit: 40% CPU, 512MB RAM)  │
│ Uses: 40% CPU, 512MB RAM                 │
│ → Stays within bounds                    │
│                                          │
│ Container B (Limit: 30% CPU, 256MB RAM)  │
│ Uses: 30% CPU, 256MB RAM                 │
│ → Stays within bounds                    │
│                                          │
│ Container C (Limit: 30% CPU, 256MB RAM)  │
│ Uses: 30% CPU, 256MB RAM                 │
│ → Fair resource distribution             │
└──────────────────────────────────────────┘

Setting Limits:
─────────────────────────────────────────────────────────────

docker run --memory="512m" \
           --memory-swap="1g" \
           --cpus="2.0" \
           --cpuset-cpus="0,1" \
           myapp

docker-compose:
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

### 4. Logging and Monitoring

```
Container Logging Architecture:
─────────────────────────────────────────────────────────────

┌──────────────────────────────────────┐
│  Application Running in Container    │
├──────────────────────────────────────┤
│  System.out/System.err (Java)        │
│  console.log (Node.js)               │
│  echo (Shell scripts)                │
└──────┬───────────────────────────────┘
       │
       │ Streams to STDOUT/STDERR
       │
       ▼
┌──────────────────────────────────────┐
│  Docker Daemon Log Driver            │
│  (Default: json-file)                │
└──────┬───────────────────────────────┘
       │
       │ Stores logs
       │
       ▼
┌──────────────────────────────────────┐
│  /var/lib/docker/containers/         │
│    [container-id]/                   │
│    [container-id]-json.log           │
│  (JSON formatted logs)               │
└──────────────────────────────────────┘

Commands:
─────────────────────────────────────────────────────────────

# View logs
docker logs container_name

# Follow logs (streaming)
docker logs -f container_name

# Last 100 lines
docker logs --tail 100 container_name

# With timestamps
docker logs --timestamps container_name

# Docker Compose logs
docker-compose logs -f backend
```

### 5. Security Concepts

```
Container Security Layers:
─────────────────────────────────────────────────────────────

1. Image Scanning
   ┌──────────────────────────────────────┐
   │ Check for vulnerable dependencies    │
   │ - Vulnerable CVE databases           │
   │ - Version mismatches                 │
   │ - Known security issues              │
   └──────────────────────────────────────┘

2. Container Isolation
   ┌──────────────────────────────────────┐
   │ Namespaces: Isolation mechanism      │
   │ - PID namespace (processes)          │
   │ - Network namespace (networking)     │
   │ - UTS namespace (hostname)           │
   │ - Mount namespace (filesystem)       │
   │ - User namespace (permissions)       │
   │ - IPC namespace (inter-process)      │
   └──────────────────────────────────────┘

3. Capabilities
   ┌──────────────────────────────────────┐
   │ Drop unnecessary Linux capabilities: │
   │ - CAP_NET_RAW (packet sniffing)     │
   │ - CAP_SYS_ADMIN (admin access)      │
   │ - CAP_SETUID (user switching)       │
   │ (Default: safe set)                  │
   └──────────────────────────────────────┘

4. Read-only Filesystem
   ┌──────────────────────────────────────┐
   │ run --read-only \                    │
   │     --tmpfs /tmp \                   │
   │     --tmpfs /var/run                 │
   │                                      │
   │ Prevents tampering with files        │
   │ Requires explicit write locations    │
   └──────────────────────────────────────┘

5. Non-root User
   ┌──────────────────────────────────────┐
   │ Dockerfile:                          │
   │ USER appuser (non-root)              │
   │                                      │
   │ Limits damage if container escaped   │
   │ Cannot modify system files           │
   └──────────────────────────────────────┘

Dockerfile Security Best Practices:
─────────────────────────────────────────────────────────────

# Bad: Running as root
FROM ubuntu:20.04
RUN apt-get update && apt-get install -y openjdk-17
CMD ["java", "-jar", "app.jar"]

# Good: Running as non-root
FROM openjdk:17-slim
RUN useradd -m -u 1000 appuser
COPY --chown=appuser:appuser app.jar /app/
USER appuser
CMD ["java", "-jar", "/app/app.jar"]

# Better: Minimal base image + Security scanning
FROM openjdk:17-slim-bullseye
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

RUN useradd -m -u 1000 appuser
COPY --chown=appuser:appuser app.jar /app/
USER appuser
HEALTHCHECK CMD curl -f http://localhost:8080/health
CMD ["java", "-Xmx512m", "-jar", "/app/app.jar"]
```

---

## Summary: How Everything Works Together

```
Complete Application Flow:
─────────────────────────────────────────────────────────────

1. DEVELOPMENT
   ┌──────────────────────────────────────┐
   │ Write Code:                          │
   │ - Spring Boot Java code              │
   │ - Angular TypeScript code            │
   │ - Docker files (Dockerfile, etc)     │
   └──────────────────────────────────────┘

2. BUILD PHASE
   ┌──────────────────────────────────────┐
   │ Backend Build:                       │
   │ mvn clean package → app.jar          │
   │ docker build → Backend Image         │
   │                                      │
   │ Frontend Build:                      │
   │ npm run build → dist/folder          │
   │ docker build → Frontend Image        │
   │                                      │
   │ Registry:                            │
   │ docker push → Docker Hub/Registry    │
   └──────────────────────────────────────┘

3. DEPLOYMENT
   ┌──────────────────────────────────────┐
   │ Create Docker Network                │
   │ docker network create                │
   │                                      │
   │ Start Containers:                    │
   │ docker run (or docker-compose up)    │
   │ ├─ MySQL Container                  │
   │ ├─ Spring Backend Container         │
   │ ├─ Angular Frontend Container       │
   │ └─ All on same network               │
   └──────────────────────────────────────┘

4. RUNTIME
   ┌──────────────────────────────────────┐
   │ Client Request → Frontend:80          │
   │ ├─ Browser loads Angular app         │
   │ ├─ Static files served by Nginx      │
   │ └─ JavaScript code runs              │
   │                                      │
   │ Frontend:80 → Backend:8080           │
   │ ├─ XHR/Fetch request                │
   │ ├─ DNS resolves "backend" service   │
   │ ├─ Request proxied to 172.18.0.3:8080
   │ └─ Spring app handles request       │
   │                                      │
   │ Backend:8080 → MySQL:3306           │
   │ ├─ Data query                       │
   │ ├─ DNS resolves "mysql" service     │
   │ ├─ Connection to 172.18.0.4:3306    │
   │ └─ MySQL executes query             │
   │                                      │
   │ Response flows back                  │
   │ MySQL → Backend → Frontend → Browser │
   └──────────────────────────────────────┘

5. MONITORING & LOGGING
   ┌──────────────────────────────────────┐
   │ docker logs -f container             │
   │ docker stats                         │
   │ docker exec -it container /bin/sh    │
   │                                      │
   │ Prometheus metrics (optional)        │
   │ ELK Stack (optional)                 │
   │ Datadog (optional)                   │
   └──────────────────────────────────────┘
```

---

## Key Takeaways

1. **Docker Image** = Blueprint/Template
2. **Docker Container** = Running instance with isolated filesystem, processes, and networking
3. **Dockerfile** = Instructions to build image (layers)
4. **Docker Network** = Enables containers to communicate using service names
5. **Port Mapping** = Maps host ports to container ports (8080:8080)
6. **Volumes** = Persistent storage outside container
7. **Multi-stage builds** = Smaller final images (build + runtime stages)
8. **Docker Compose** = Easy way to run multi-container applications
9. **Service Names** = Internal DNS (backend, mysql) instead of IPs
10. **Health Checks** = Monitor container health

---

## Useful Docker Commands Reference

```bash
# Image commands
docker build -t image-name:tag .           # Build image
docker images                              # List images
docker rmi image-name                      # Remove image
docker inspect image-name                  # Inspect image

# Container commands
docker run -d -p 8080:8080 image-name      # Run container
docker ps                                  # List running containers
docker ps -a                               # List all containers
docker stop container-id                   # Stop container
docker start container-id                  # Start container
docker remove container-id                 # Remove container
docker logs -f container-name              # View logs
docker exec -it container-name /bin/bash   # Execute command

# Network commands
docker network ls                          # List networks
docker network create my-network           # Create network
docker network inspect my-network          # Inspect network

# Volume commands
docker volume ls                           # List volumes
docker volume create my-volume             # Create volume
docker volume inspect my-volume            # Inspect volume

# Docker Compose commands
docker-compose up -d                       # Start services
docker-compose down                        # Stop services
docker-compose logs -f service-name        # View logs
docker-compose ps                          # List services
```

---

This comprehensive guide covers Docker from fundamentals to advanced concepts with clear explanations and architectural diagrams!

