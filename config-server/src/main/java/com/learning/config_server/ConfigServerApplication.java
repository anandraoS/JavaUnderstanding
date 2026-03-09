package com.learning.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Centralized Configuration Server
 * Demonstrates: Externalized configuration, Spring Cloud Config
 *
 * Key Concepts:
 * - Centralized configuration management for all microservices
 * - Support for multiple environments (dev, staging, prod)
 * - Dynamic configuration refresh without restart
 * - Can use Git, filesystem, or vault as backend
 * - Version control for configurations
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
