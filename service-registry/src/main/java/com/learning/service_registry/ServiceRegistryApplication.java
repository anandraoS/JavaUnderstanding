package com.learning.service_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Registry using Netflix Eureka
 * Demonstrates: Service Discovery, Service Registration
 *
 * Key Concepts:
 * - Eureka Server acts as a service registry
 * - Microservices register themselves with Eureka on startup
 * - Services can discover other services through Eureka
 * - Provides high availability and load balancing capabilities
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
	}

}
