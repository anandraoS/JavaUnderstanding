# Docker Commands Reference Guide

## Image Commands

### Building Images

```bash
# Build image from current directory Dockerfile
docker build -t image-name:tag .

# Build from specific Dockerfile
docker build -t my-app:v1.0 -f Dockerfile .

# Build with build arguments
docker build --build-arg BASE_IMAGE=openjdk:17 -t my-app:v1.0 .

# Build with no cache (rebuild all layers)
docker build --no-cache -t my-app:v1.0 .

# Build and push to registry
docker build -t myregistry.com/my-app:v1.0 . && docker push myregistry.com/my-app:v1.0
```

### Managing Images

```bash
# List all images
docker images

# List images with size
docker images --all

# Remove image
docker rmi image-name:tag

# Remove unused images
docker image prune

# Inspect image details
docker inspect image-name:tag

# Show image history (layers)
docker history image-name:tag

# Search Docker Hub
docker search ubuntu

# Pull image from registry
docker pull image-name:tag

# Push image to registry
docker push myregistry.com/image-name:tag

# Tag image with new name
docker tag old-name:tag new-name:tag
```

---

## Container Commands

### Running Containers

```bash
# Run container (basic)
docker run image-name

# Run container in background (detached)
docker run -d image-name

# Run with custom container name
docker run -d --name my-container image-name

# Run with environment variables
docker run -d -e VAR1=value1 -e VAR2=value2 image-name

# Run with port mapping
docker run -d -p 8080:8080 image-name          # host:container
docker run -d -p 127.0.0.1:8080:8080 image-name  # Bind to localhost only

# Run with volume mount
docker run -d -v /host/path:/container/path image-name
docker run -d -v myvolume:/app/data image-name

# Run with multiple configurations
docker run -d \
  --name backend \
  --network mynetwork \
  -p 8080:8080 \
  -v app-logs:/app/logs \
  -e DATABASE_URL=mysql://host:3306/db \
  -e JAVA_OPTS="-Xmx512m" \
  my-app:v1.0

# Run interactive (connect to terminal)
docker run -it image-name /bin/bash

# Run with resource limits
docker run -d --memory="512m" --cpus="2.0" image-name

# Run with restart policy
docker run -d --restart=unless-stopped image-name
docker run -d --restart=always image-name
docker run -d --restart=on-failure:5 image-name  # Restart max 5 times

# Run with health check
docker run -d \
  --health-cmd="curl -f http://localhost:8080/health || exit 1" \
  --health-interval=30s \
  --health-timeout=3s \
  --health-retries=3 \
  image-name

# Run privileged (elevated permissions)
docker run -d --privileged image-name
```

### Container Management

```bash
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# List container IDs only
docker ps -q

# View container logs
docker logs container-name

# View last 100 lines
docker logs --tail=100 container-name

# Follow logs (streaming, like tail -f)
docker logs -f container-name

# View logs with timestamps
docker logs --timestamps container-name

# View logs for last 10 minutes
docker logs --since=10m container-name

# Get container statistics (CPU, memory)
docker stats

# Get specific container stats
docker stats container-name

# Inspect container details
docker inspect container-name
docker inspect --format='{{.NetworkSettings.IPAddress}}' container-name

# Pause container (freeze processes)
docker pause container-name

# Unpause container
docker unpause container-name

# Stop container (graceful shutdown, 10s timeout)
docker stop container-name

# Kill container (immediate, no grace)
docker kill container-name

# Restart container
docker restart container-name

# Remove container
docker rm container-name

# Remove stopped containers
docker container prune

# Remove all stopped containers
docker rm $(docker ps -aq)

# Start stopped container
docker start container-name

# View container process list
docker top container-name

# Get container events
docker events --filter type=container
```

---

## Executing Commands in Containers

```bash
# Execute command in running container
docker exec container-name ls -la

# Execute command with interactivity
docker exec -it container-name bash

# Execute command as specific user
docker exec -u 1000 container-name whoami

# Execute with environment variables
docker exec -e VAR=value container-name echo $VAR

# Run multiple commands
docker exec -it container-name bash -c "echo Hello && ls -la"

# Execute in background
docker exec -d container-name command

# Copy files from container to host
docker cp container-name:/app/file.txt ./local-file.txt

# Copy files from host to container
docker cp ./local-file.txt container-name:/app/file.txt
```

---

## Network Commands

```bash
# List networks
docker network ls

# Create bridge network
docker network create my-network

# Create network with specific subnet
docker network create --subnet=172.28.0.0/16 my-network

# Connect container to network
docker network connect my-network container-name

# Disconnect container from network
docker network disconnect my-network container-name

# Inspect network details
docker network inspect my-network

# Remove network (containers must be disconnected)
docker network rm my-network

# Test network connectivity
docker exec container1 ping container2  # By container name in same network
```

---

## Volume Commands

```bash
# List volumes
docker volume ls

# Create volume
docker volume create my-volume

# Inspect volume
docker volume inspect my-volume

# Remove volume
docker volume rm my-volume

# Remove unused volumes
docker volume prune

# Mount volume to container
docker run -v my-volume:/app/data image-name

# Mount with read-only
docker run -v my-volume:/app/data:ro image-name

# Mount host directory
docker run -v /host/path:/app/data image-name
```

---

## Docker Compose Commands

```bash
# Start services
docker-compose up

# Start in background
docker-compose up -d

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View services status
docker-compose ps

# View logs
docker-compose logs

# View specific service logs
docker-compose logs -f backend

# Build services
docker-compose build

# Build specific service
docker-compose build backend

# Rebuild and restart
docker-compose up -d --build

# Execute command in service
docker-compose exec backend bash

# Restart service
docker-compose restart backend

# Stop service
docker-compose stop backend

# Start service
docker-compose start backend

# View resource usage
docker-compose stats

# Remove containers (keep volumes)
docker-compose down

# Scale service to multiple instances
docker-compose up -d --scale web=3
```

---

## Image Registry Commands

```bash
# Login to Docker Hub
docker login

# Login to private registry
docker login myregistry.com

# Logout
docker logout

# Push image
docker push myregistry.com/my-app:v1.0

# Pull image
docker pull myregistry.com/my-app:v1.0

# Save image to tar file
docker save -o my-image.tar image-name:tag

# Load image from tar file
docker load -i my-image.tar
```

---

## System Commands

```bash
# Show Docker system info
docker system info

# Show disk usage (images, containers, volumes)
docker system df

# Remove unused resources (images, containers, networks, volumes)
docker system prune

# Remove unused images
docker image prune

# Remove dangling images (untagged)
docker image prune -a

# View Docker version
docker version

# View Docker daemon logs
docker logs --help  # Note: This is for container logs
# On Linux: sudo journalctl -u docker
# On Docker Desktop: Use Docker Desktop UI

# Check Docker daemon status
docker ps  # If this works, daemon is running
```

---

## Useful Combinations

### Debug Container
```bash
# Get detailed info about container
docker ps -a
docker logs container-id
docker exec -it container-id /bin/bash
# Inside container: exit
```

### Monitor Container
```bash
# Watch container in real-time
docker stats --no-stream=false

# Monitor with details
docker stats --all
```

### Clean Up Everything
```bash
# Remove all stopped containers
docker container prune -f

# Remove all unused images
docker image prune -a -f

# Remove all unused volumes
docker volume prune -f

# Remove all unused networks
docker network prune -f

# WARNING: Remove EVERYTHING unused
docker system prune -a -f --volumes
```

### Find Container by Port
```bash
# Find container using port 8080
docker ps --format "table {{.ID}}\t{{.Ports}}" | grep 8080

# Get container name using port
docker ps --filter "ancestor=my-image:tag" --format "{{.Names}}"
```

### Restart Services with Dependencies
```bash
# In docker-compose, restart backend and dependents
docker-compose up -d --no-deps backend
```

---

## Docker File Locations

### On Linux
```
Docker root directory: /var/lib/docker/
Container data: /var/lib/docker/containers/
Images: /var/lib/docker/images/
Volumes: /var/lib/docker/volumes/
```

### On Windows (Docker Desktop)
```
Data stored in Hyper-V VM
Access through Docker CLI only
```

### On macOS (Docker Desktop)
```
Data stored in Linux VM
Access through Docker CLI only
```

---

## Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| `Port already in use` | Another service using port | `docker ps` to find container, change port in config |
| `Connection refused` | Service not running | `docker ps` to verify, `docker logs` to check errors |
| `Cannot find container` | Typo in name or removed | `docker ps -a` to list all, recreate if removed |
| `Out of memory` | Container memory limit exceeded | Increase limit: `--memory="1g"` |
| `Network unreachable` | Containers not on same network | `docker network connect` to same network |
| `Volume not found` | Named volume doesn't exist | `docker volume create volume-name` |
| `Permission denied` | Running as wrong user | Use `-u user-id:group-id` or `chmod` |

---

## Performance Tips

```bash
# Use .dockerignore to exclude files (like .gitignore)
cat > .dockerignore << EOF
.git
node_modules
.env
.DS_Store
EOF

# Use multi-stage builds to reduce image size
# See Dockerfile examples in project

# Layer caching: Order Dockerfile instructions
# Put frequently changing instructions last

# Prune regularly
docker system prune -a -f --volumes
```

---

This reference covers most common Docker commands. For more details, run:
```bash
docker --help
docker <command> --help
docker-compose --help
```

