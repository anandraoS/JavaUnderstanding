package com.learning.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration for Order Service
 * Demonstrates: Thread pool management, @Async processing
 *
 * CONCEPTS:
 * 1. ThreadPoolTaskExecutor — manages a pool of background threads
 * 2. corePoolSize = 10 — always keep 10 threads ready
 * 3. maxPoolSize = 20 — scale up to 20 under heavy load
 * 4. queueCapacity = 200 — queue up to 200 tasks before rejecting
 * 5. "async-order-" prefix — thread names in logs for debugging
 *
 * Used by: OrderService.processOrderAsync() — processes orders in background
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-order-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}

