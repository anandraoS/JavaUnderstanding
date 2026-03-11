# Docker Architecture Deep Dive with Detailed Diagrams

## 1. Docker Core Architecture

### High-Level Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                    Computer/Server (Host)                        │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Docker Installation                        │   │
│  │                                                         │   │
│  │  ┌────────────────┐    ┌──────────────────┐           │   │
│  │  │  Docker Client │    │  Docker Daemon   │           │   │
│  │  │  (CLI)         │◄──►│  (dockerd)       │           │   │
│  │  │                │    │                  │           │   │
│  │  │ $ docker run   │    │ Manages:         │           │   │
│  │  │ $ docker ps    │    │ - Containers     │           │   │
│  │  │ $ docker build │    │ - Images         │           │   │
│  │  │ $ docker logs  │    │ - Networks       │           │   │
│  │  │                │    │ - Volumes        │           │   │
│  │  └────────────────┘    └────────┬─────────┘           │   │
│  │                                 │                      │   │
│  │                    ┌────────────┴────────────┐        │   │
│  │                    ▼                         ▼        │   │
│  │              ┌──────────────┐         ┌──────────┐   │   │
│  │              │   Registry   │         │Container │   │   │
│  │              │ (Docker Hub) │         │ Runtime  │   │   │
│  │              │              │         │(containerd)   │   │
│  │              │ Store/Pull   │         │          │   │   │
│  │              │ Images       │         │Runs      │   │   │
│  │              │              │         │containers   │   │
│  │              └──────────────┘         └──────────┘   │   │
│  │                                                       │   │
│  │  ┌─────────────────────────────────────────────┐    │   │
│  │  │         Containerization Infrastructure     │    │   │
│  │  │                                             │    │   │
│  │  │  Namespaces (Isolation):                   │    │   │
│  │  │  ├─ PID Namespace (Process isolation)      │    │   │
│  │  │  ├─ Network Namespace (Network isolation)  │    │   │
│  │  │  ├─ Mount Namespace (Filesystem isolation) │    │   │
│  │  │  ├─ IPC Namespace (Inter-process)          │    │   │
│  │  │  ├─ UTS Namespace (Hostname)               │    │   │
│  │  │  └─ User Namespace (User isolation)        │    │   │
│  │  │                                             │    │   │
│  │  │  cgroups (Resource Limits):                │    │   │
│  │  │  ├─ CPU limits                             │    │   │
│  │  │  ├─ Memory limits                          │    │   │
│  │  │  ├─ Disk I/O limits                        │    │   │
│  │  │  └─ Network bandwidth limits               │    │   │
│  │  └─────────────────────────────────────────────┘    │   │
│  │                                                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           Host Operating System (Linux)            │   │
│  │           (Kernel handles containerization)       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────────┘
```

## 2. Image Building Process

### Dockerfile to Running Container

```
Step 1: Dockerfile Interpretation
┌──────────────────────────────┐
│ Dockerfile                   │
├──────────────────────────────┤
│ FROM openjdk:17-slim         │ ← Base Image
│ WORKDIR /app                 │ ← Set directory
│ COPY app.jar app.jar         │ ← Copy file
│ EXPOSE 8080                  │ ← Port info
│ CMD ["java","-jar",...]      │ ← Default command
└──────────────────────────────┘
          │
          │ docker build
          ▼

Step 2: Layer Building
┌─────────────────────────────────────────────┐
│  Image Layers (Immutable)                   │
├─────────────────────────────────────────────┤
│ Layer 0 (Base): openjdk:17-slim             │ ~800MB
├─────────────────────────────────────────────┤
│ Layer 1: WORKDIR /app                       │ (metadata)
├─────────────────────────────────────────────┤
│ Layer 2: COPY app.jar app.jar               │ ~50MB
├─────────────────────────────────────────────┤
│ Layer 3: EXPOSE 8080                        │ (metadata)
├─────────────────────────────────────────────┤
│ Layer 4: CMD command                        │ (metadata)
├─────────────────────────────────────────────┤
│ Image ID: sha256:abc123...                  │
└─────────────────────────────────────────────┘
          │
          │
          ▼

Step 3: Running Container
┌─────────────────────────────────────────────┐
│  Container (Image + Writable Layer)         │
├─────────────────────────────────────────────┤
│ Layer 5 (RW): /app/logs/ (files created)    │
│               /tmp/ (temp files)            │
├─────────────────────────────────────────────┤
│ Layer 4: CMD (readonly)                     │
├─────────────────────────────────────────────┤
│ Layer 3: EXPOSE (readonly)                  │
├─────────────────────────────────────────────┤
│ Layer 2: COPY app.jar (readonly)            │
├─────────────────────────────────────────────┤
│ Layer 1: WORKDIR (readonly)                 │
├─────────────────────────────────────────────┤
│ Layer 0: openjdk:17-slim (readonly)         │
├─────────────────────────────────────────────┤
│ Container ID: a1b2c3d4e5f6...               │
│ Running Process: PID 1 (Java application)   │
└─────────────────────────────────────────────┘
```

## 3. Container Lifecycle with Process Isolation

### Process Namespace Isolation

```
┌─────────────────────────────────┐      ┌──────────────────────────┐
│   Container 1 (Frontend)        │      │   Container 2 (Backend)  │
├─────────────────────────────────┤      ├──────────────────────────┤
│                                 │      │                          │
│  Process Tree (PID Namespace)   │      │  Process Tree            │
│  ──────────────────────────────│      │  ──────────────────      │
│                                 │      │                          │
│  PID 1: nginx (main)            │      │  PID 1: java             │
│    ├─ PID 2: nginx worker       │      │    ├─ PID 2: thread 1   │
│    ├─ PID 3: nginx worker       │      │    ├─ PID 3: thread 2   │
│    └─ PID 4: bash               │      │    └─ PID 4: thread 3   │
│                                 │      │                          │
│  Network Stack (isolated):      │      │  Network Stack:          │
│  - IP: 172.18.0.2              │      │  - IP: 172.18.0.3       │
│  - Ports: 80 (listening)       │      │  - Ports: 8080 (listening)
│  - Eth0: veth-xxx              │      │  - Eth0: veth-yyy       │
│                                 │      │                          │
│  Filesystem (isolated):         │      │  Filesystem:             │
│  - /etc/nginx/                  │      │  - /app/                │
│  - /var/cache/nginx/            │      │  - /usr/local/openjdk/  │
│  - /var/run/                    │      │  - /tmp/                │
│                                 │      │                          │
└─────────────────────────────────┘      └──────────────────────────┘
            │                                      │
            │ Virtual Ethernet                     │
            │ veth-xxx:172.18.0.2                 │ veth-yyy:172.18.0.3
            │                                     │
            └─────────────┬───────────────────────┘
                          │
            ┌─────────────┴──────────────┐
            ▼                            ▼
    ┌──────────────────┐        ┌──────────────────┐
    │  Docker Bridge   │        │  Host Network    │
    │  (docker0)       │        │  Interface       │
    │  172.18.0.0/16   │        │                  │
    │                  │        │  Port Bindings:  │
    │  ├─ Container1   │        │  Host:80 →       │
    │  │ 172.18.0.2    │        │  Container:80    │
    │  │                         │  (veth-xxx:80)   │
    │  ├─ Container2   │        │                  │
    │  │ 172.18.0.3    │        │  Host:8080 →     │
    │  │                         │  Container:8080  │
    │  └─ MySQL        │        │  (veth-yyy:8080) │
    │    172.18.0.4    │        │                  │
    └──────────────────┘        └──────────────────┘
```

## 4. Network Communication Architecture

### DNS Resolution Inside Containers

```
┌─────────────────────────────────────────────────────────────────┐
│  Frontend Container (Angular + Nginx)                           │
│                                                                 │
│  Code: fetch('http://backend:8080/api/data')                   │
│                                                                 │
│  Step 1: DNS Lookup                                             │
│  ├─ Query: "What is backend?"                                  │
│  ├─ DNS Resolver in Container: 127.0.0.11:53                  │
│  │  (Embedded DNS server, runs in container)                   │
│  └─ Response: "backend is 172.18.0.3"                          │
│                                                                 │
│  Step 2: TCP Connection                                         │
│  ├─ Create socket to 172.18.0.3:8080                           │
│  ├─ Send SYN packet through veth-xxx interface                │
│  └─ Establish 3-way handshake                                  │
│                                                                 │
│  Step 3: Send HTTP Request                                      │
│  ├─ HTTP GET /api/data                                         │
│  ├─ Headers: Host: backend:8080                                │
│  └─ Sent through Docker bridge to backend container            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                           │
                           │ Docker Bridge (docker0)
                           │ 172.18.0.0/16 network
                           │
┌─────────────────────────────────────────────────────────────────┐
│  Backend Container (Spring Boot)                                │
│                                                                 │
│  Step 4: Receive HTTP Request                                   │
│  ├─ Nginx container receives on veth-yyy:80 (EXTERNAL)        │
│  ├─ But Spring Boot listens on :8080 (INTERNAL)              │
│  ├─ Nginx proxy_pass http://backend:8080                      │
│  └─ Routes to localhost:8080 inside backend container         │
│                                                                 │
│  Step 5: Process Request                                        │
│  ├─ Spring Boot controller receives request                    │
│  ├─ Query database: jdbc:mysql://mysql:3306/mydb              │
│  ├─ MySQL DNS resolves to 172.18.0.4                          │
│  ├─ Connect to MySQL, fetch data                              │
│  └─ Build JSON response                                         │
│                                                                 │
│  Step 6: Send Response                                          │
│  ├─ HTTP 200 OK with JSON body                                │
│  ├─ Sent back through Docker bridge                           │
│  └─ Frontend receives response                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                           │
                           │
┌─────────────────────────────────────────────────────────────────┐
│  MySQL Container (Database)                                     │
│                                                                 │
│  Step 7: Execute Query                                          │
│  ├─ MySQL server receives SQL query                            │
│  ├─ Find relevant data in database files                       │
│  ├─ Execute query (SELECT, INSERT, UPDATE, DELETE)            │
│  └─ Return results                                             │
│                                                                 │
│  Step 8: Send Response Back                                     │
│  ├─ Results sent to Backend container                          │
│  └─ Backend processes and sends to Frontend                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 5. Volume Storage Architecture

### How Volumes Work

```
┌────────────────────────────────────────────────────────────────┐
│                    Host Machine                               │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  Filesystem Options:                                          │
│                                                                │
│  1. Named Volume                                              │
│  ┌──────────────────────────────┐                             │
│  │ /var/lib/docker/volumes/     │                             │
│  │   mysql_data/_data/          │                             │
│  │   ├─ ibdata1                 │                             │
│  │   ├─ mysql/ (system db)       │                             │
│  │   ├─ mydb/ (user db)          │                             │
│  │   └─ ib_logfile0              │                             │
│  │                               │                             │
│  │  Managed by Docker            │                             │
│  │  Easy to backup               │                             │
│  │  Survives container deletion  │                             │
│  └──────────────────────────────┘                             │
│           │ (Mount)                                           │
│           │ ONLY MySQL container can access                  │
│           │ Cannot be accessed directly from host            │
│           ▼                                                   │
│  ┌───────────────────────────────┐                            │
│  │  MySQL Container              │                            │
│  │  /var/lib/mysql/ (inside)     │                            │
│  │  ├─ ibdata1                   │                            │
│  │  ├─ mysql/                    │                            │
│  │  ├─ mydb/                     │                            │
│  │  └─ ib_logfile0               │                            │
│  │                               │                            │
│  │  Persists data outside        │                            │
│  │  container lifecycle          │                            │
│  └───────────────────────────────┘                            │
│                                                                │
│  2. Bind Mount                                                │
│  ┌──────────────────────────────┐                             │
│  │ /home/user/myapp-config/     │                             │
│  │ ├─ config.yaml                │                             │
│  │ ├─ secrets.env                │                             │
│  │ └─ certificates/              │                             │
│  │                               │                             │
│  │  Host managed files           │                             │
│  │  Direct access from both      │                             │
│  │  host and container           │                             │
│  └──────────────────────────────┘                             │
│           │ (Mount)                                           │
│           │ Both can read/write                               │
│           │ Host changes reflect in container                │
│           ▼                                                   │
│  ┌───────────────────────────────┐                            │
│  │  Spring Boot Container        │                            │
│  │  /app/config/ (inside)        │                            │
│  │  ├─ config.yaml               │                            │
│  │  ├─ secrets.env               │                            │
│  │  └─ certificates/             │                            │
│  │                               │                            │
│  │  Can access host files        │                            │
│  │  Changes made here affect     │                            │
│  │  host filesystem              │                            │
│  └───────────────────────────────┘                            │
│                                                                │
│  3. Tmpfs Mount                                               │
│  ┌──────────────────────────────┐                             │
│  │  RAM (In-Memory)             │                             │
│  │  /dev/shm/                   │                             │
│  │  ├─ cache/                   │                             │
│  │  ├─ session_data/            │                             │
│  │  └─ temp.txt                 │                             │
│  │                               │                             │
│  │  Ultra-fast (RAM speed)       │                             │
│  │  Lost when container stops    │                             │
│  │  Perfect for caches           │                             │
│  └──────────────────────────────┘                             │
│           │ (Mount)                                           │
│           │ Container uses RAM for storage                   │
│           ▼                                                   │
│  ┌───────────────────────────────┐                            │
│  │  Backend Container            │                            │
│  │  /app/cache/ (in RAM)         │                            │
│  │  ├─ session_data/             │                            │
│  │  ├─ temp_files/               │                            │
│  │  └─ [cleared on restart]      │                            │
│  │                               │                            │
│  │  Extremely fast access        │                            │
│  │  No persistence               │                            │
│  │  Data loss is acceptable      │                            │
│  └───────────────────────────────┘                            │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

## 6. Multi-Container Communication Flow

### Complete Request-Response Cycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                             │
│                    User: Click "Get Data" Button                    │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ HTTP GET http://localhost/api/data
                         │ (Host Machine:80)
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  PORT BINDING LAYER                                                 │
│  Host:80 → Container:80 (veth-interface)                            │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  FRONTEND CONTAINER (Nginx)                                         │
│  Service name: "frontend"                                           │
│  IP: 172.18.0.2:80                                                  │
│                                                                     │
│  1. Receive HTTP request on :80                                     │
│  2. Parse: GET /api/data                                           │
│  3. Check nginx.conf routing rules                                 │
│  4. Found: location /api/                                          │
│  5. Rule: proxy_pass http://backend:8080                           │
│  6. Perform DNS resolution                                         │
│      Query: "resolve 'backend'"                                    │
│      Answer: 172.18.0.3:8080                                       │
│                                                                     │
│  ├─ Docker Embedded DNS (127.0.0.11:53)                           │
│  │  Queries Service Discovery database                            │
│  │  frontend  → 172.18.0.2                                        │
│  │  backend   → 172.18.0.3                                        │
│  │  mysql     → 172.18.0.4                                        │
│  └─ Returns: 172.18.0.3                                            │
│                                                                     │
│  7. Connect to 172.18.0.3:8080                                     │
│  8. Forward HTTP request                                            │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ Docker Bridge Network (docker0)
                         │ Virtual ethernet: veth-xxx → veth-yyy
                         │ Packet routed through bridge
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  BACKEND CONTAINER (Spring Boot Java)                              │
│  Service name: "backend"                                            │
│  IP: 172.18.0.3:8080                                                │
│                                                                     │
│  1. Nginx receives request on veth interface                       │
│  2. Nginx acts as reverse proxy                                    │
│     (sits between frontend and backend)                            │
│  3. Re-sends request to backend                                    │
│                                                                     │
│  4. Spring Boot Controller receives request                         │
│     GET /api/data HTTP/1.1                                         │
│     Host: backend:8080                                             │
│                                                                     │
│  5. Controller method executes:                                     │
│     @GetMapping("/api/data")                                       │
│     public Data getData() { ... }                                  │
│                                                                     │
│  6. Requires data from database:                                   │
│     Need to connect to MySQL                                       │
│                                                                     │
│  7. DataSource configuration:                                       │
│     jdbc:mysql://mysql:3306/mydb                                  │
│     username: appuser                                              │
│     password: apppassword                                          │
│                                                                     │
│  8. JDBC Driver resolves "mysql"                                   │
│     Query Docker DNS: "resolve 'mysql'"                            │
│     Answer: 172.18.0.4:3306                                        │
│                                                                     │
│  9. TCP Connection to MySQL:                                        │
│     ├─ Create socket to 172.18.0.4:3306                           │
│     ├─ 3-way handshake (SYN, SYN-ACK, ACK)                        │
│     ├─ Send MySQL authentication packet                            │
│     └─ Connection established                                      │
│                                                                     │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ Docker Bridge Network (docker0)
                         │ Packet routing between containers
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  MYSQL CONTAINER (Database)                                         │
│  Service name: "mysql"                                              │
│  IP: 172.18.0.4:3306                                                │
│  Volume mount: /var/lib/mysql (Named volume: mysql_data)           │
│                                                                     │
│  1. MySQL server listens on :3306                                  │
│  2. Receives connection request from 172.18.0.3:xxxxx              │
│  3. Authenticates using provided credentials                       │
│     Username: appuser                                              │
│     Password: apppassword (verified from MySQL user table)         │
│                                                                     │
│  4. Spring Boot sends SQL query:                                    │
│     SELECT * FROM users WHERE id = 1;                             │
│                                                                     │
│  5. MySQL query processor:                                          │
│     ├─ Parse SQL syntax                                            │
│     ├─ Optimize query plan                                         │
│     ├─ Access storage engine (InnoDB)                             │
│     └─ Fetch rows from /var/lib/mysql/mydb/users.ibd             │
│                                                                     │
│  6. Build result set:                                              │
│     id=1, name="John", email="john@example.com"                   │
│                                                                     │
│  7. Send query result back:                                         │
│     Result set sent through socket to backend container           │
│                                                                     │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ Query Result (JSON-like)
                         │ Sent back through socket
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  BACKEND CONTAINER (Spring Boot Java)                              │
│  (Continued from step 9)                                            │
│                                                                     │
│  10. Receive query result from MySQL                                │
│  11. Process result set with Spring Data JPA/Hibernate            │
│       ├─ Convert database rows to Java objects                    │
│       ├─ Apply business logic                                      │
│       └─ Format as JSON                                            │
│                                                                     │
│  12. Create JSON response:                                          │
│       {                                                             │
│         "id": 1,                                                   │
│         "name": "John",                                            │
│         "email": "john@example.com"                               │
│       }                                                             │
│                                                                     │
│  13. Send HTTP response back to Nginx proxy                         │
│       HTTP/1.1 200 OK                                              │
│       Content-Type: application/json                              │
│       [JSON body]                                                  │
│                                                                     │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ Through Docker Bridge (docker0)
                         │ Response packet routed back
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│  FRONTEND CONTAINER (Nginx)                                         │
│                                                                     │
│  1. Receive HTTP response from backend (172.18.0.3:8080)          │
│  2. Nginx proxy buffer fills with response                         │
│  3. Nginx forwards response to client:                             │
│     HTTP/1.1 200 OK                                                │
│     Content-Type: application/json                                │
│     [JSON body]                                                    │
│                                                                     │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         │ PORT BINDING LAYER
                         │ Container:80 → Host:80
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                             │
│                                                                     │
│  Browser receives HTTP 200 response with JSON data                 │
│  JavaScript code (Angular) processes response:                     │
│  ├─ Parse JSON                                                     │
│  ├─ Update component state                                         │
│  ├─ Refresh UI                                                     │
│  └─ Display data to user                                           │
│                                                                     │
│  User sees: "Data from database successfully retrieved!"          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

TOTAL TIME: ~100-200ms (depending on network, database size, processing)
```

## 7. Container Resource Isolation

### cgroups (Control Groups)

```
┌──────────────────────────────────────────────────────────────────┐
│               Host System Resources                              │
│                                                                  │
│  Total: 8 CPU cores, 16GB RAM, 500GB Disk                      │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  cgroup Hierarchy                                          │ │
│  │  /sys/fs/cgroup/                                          │ │
│  │                                                            │ │
│  │  ├─ cpu/ (CPU limits)                                     │ │
│  │  │  ├─ container1_abc/                                   │ │
│  │  │  │  └─ cpu.max = "200000 1000000" (2 cores)          │ │
│  │  │  ├─ container2_def/                                   │ │
│  │  │  │  └─ cpu.max = "100000 1000000" (1 core)           │ │
│  │  │  └─ container3_ghi/                                   │ │
│  │  │     └─ cpu.max = "100000 1000000" (1 core)           │ │
│  │  │                                                        │ │
│  │  ├─ memory/ (Memory limits)                              │ │
│  │  │  ├─ container1_abc/                                   │ │
│  │  │  │  └─ memory.max = 8589934592 (8GB)                 │ │
│  │  │  ├─ container2_def/                                   │ │
│  │  │  │  └─ memory.max = 4294967296 (4GB)                 │ │
│  │  │  └─ container3_ghi/                                   │ │
│  │  │     └─ memory.max = 2147483648 (2GB)                 │ │
│  │  │                                                        │ │
│  │  ├─ blkio/ (Disk I/O limits)                             │ │
│  │  │  ├─ container1_abc/                                   │ │
│  │  │  │  └─ blkio.throttle.read_bps_device = 10485760    │ │
│  │  │  └─ ...                                               │ │
│  │  │                                                        │ │
│  │  └─ net_cls/ (Network limits)                            │ │
│  │     └─ ...                                               │ │
│  │                                                            │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  Resource Allocation at Runtime                           │ │
│  │                                                            │ │
│  │  Container1 (Frontend - Nginx)        [2 CPU cores]       │ │
│  │  ████████░░░░░░░░░░░░░░░░░░░░░░░░  Limit: 8GB RAM       │ │
│  │  Currently: Using 1.5 CPU, 1.2GB RAM                     │ │
│  │                                                            │ │
│  │  Container2 (Backend - Spring Boot)   [1 CPU core]        │ │
│  │  ████████░░░░░░░░░░░░░░░░░░░░░░░░  Limit: 4GB RAM       │ │
│  │  Currently: Using 0.8 CPU, 2.5GB RAM                     │ │
│  │                                                            │ │
│  │  Container3 (Database - MySQL)       [1 CPU core]         │ │
│  │  ████░░░░░░░░░░░░░░░░░░░░░░░░░░░  Limit: 2GB RAM        │ │
│  │  Currently: Using 0.3 CPU, 1.8GB RAM                     │ │
│  │                                                            │ │
│  │  Unused Resources                                          │ │
│  │  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  4 CPU cores, 8.5GB   │ │
│  │                                                            │ │
│  │  If Container1 tries to use > 2 cores:                    │ │
│  │  └─ cgroup throttles CPU to 2 cores                       │ │
│  │                                                            │ │
│  │  If Container2 tries to use > 4GB RAM:                    │ │
│  │  └─ cgroup kills process (OOM - Out of Memory)           │ │
│  │                                                            │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## 8. Complete System Stack

### Full Docker Ecosystem

```
                           ┌─────────────────────┐
                           │  Client Application │
                           │  (Browser/Client)   │
                           └──────────┬──────────┘
                                      │
                      HTTP/HTTPS over Network
                                      │
            ┌─────────────────────────┴─────────────────────────┐
            ▼                                                    ▼
    ┌──────────────────────────┐                    ┌──────────────────────────┐
    │  Load Balancer           │                    │  Docker Registry         │
    │  (nginx/haproxy)         │                    │  (Docker Hub / Private)  │
    │  Distributes traffic     │                    │                          │
    │  to multiple hosts       │                    │  Stores images:          │
    └──────────────┬───────────┘                    │  - openjdk:17            │
                   │                                │  - nginx:alpine          │
        ┌──────────┴──────────┐                     │  - mysql:8.0            │
        │                     │                     │  - custom-app:v1.0      │
        ▼                     ▼                     └──────────────────────────┘
    ┌─────────────┐      ┌─────────────┐
    │   Host 1    │      │   Host 2    │
    │             │      │             │
    │ Docker      │      │ Docker      │
    │ Engine      │      │ Engine      │
    │             │      │             │
    │ Containers: │      │ Containers: │
    │ ┌─────────┐ │      │ ┌─────────┐ │
    │ │Frontend │ │      │ │Frontend │ │
    │ │:80      │ │      │ │:80      │ │
    │ └─────────┘ │      │ └─────────┘ │
    │             │      │             │
    │ ┌─────────┐ │      │ ┌─────────┐ │
    │ │Backend  │ │      │ │Backend  │ │
    │ │:8080    │ │      │ │:8080    │ │
    │ └─────────┘ │      │ └─────────┘ │
    │             │      │             │
    │ ┌─────────┐ │      │ ┌─────────┐ │
    │ │MySQL    │ │      │ │Redis    │ │
    │ │:3306    │ │      │ │:6379    │ │
    │ └─────────┘ │      │ └─────────┘ │
    │             │      │             │
    │ Networks:   │      │ Networks:   │
    │ app-net     │      │ app-net     │
    │             │      │             │
    │ Volumes:    │      │ Volumes:    │
    │ data, logs  │      │ cache       │
    │             │      │             │
    └─────────────┘      └─────────────┘
```

---

This visual guide helps understand the complete Docker architecture and how all components interact!

