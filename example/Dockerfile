# Production-Ready Dockerfile for Spring Boot Application
# This file demonstrates best practices for containerizing Spring Boot apps

FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /build

# Copy pom.xml for dependency resolution
COPY pom.xml .

# Cache dependencies (this layer is reused if pom.xml hasn't changed)
RUN mvn dependency:resolve

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ============= RUNTIME STAGE =============
# Use minimal Java image for smaller final image
FROM openjdk:17-slim

# Set working directory in container
WORKDIR /app

# Create non-root user for security (best practice)
RUN useradd -m -u 1000 appuser

# Copy built JAR from builder stage
COPY --from=builder --chown=appuser:appuser /build/target/example-0.0.1-SNAPSHOT.jar app.jar

# Expose port (documentation only, doesn't bind)
EXPOSE 8080

# Set Java options for memory management
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# Health check (optional but recommended for production)
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher --health || exit 1

# Switch to non-root user
USER appuser

# Configure logging to stdout/stderr (important for Docker)
# Set Spring profile (Docker-specific configuration)
ENV SPRING_PROFILES_ACTIVE=docker

# Start the application
# Using exec form (PID 1) for proper signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]

