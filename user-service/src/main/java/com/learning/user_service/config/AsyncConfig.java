package com.learning.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * ═══════════════════════════════════════════════════════════════════
 * ASYNC CONFIGURATION (Background Thread Pool)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Asynchronous processing with @Async, Thread pool management
 *
 * CONCEPT — WHY ASYNC?
 *   Without async (synchronous):
 *     POST /users → create user (5ms)
 *                 → write audit to MySQL (20ms)      ← BLOCKS
 *                 → publish Kafka event (15ms)        ← BLOCKS
 *                 → RESPOND to client (total: 40ms)
 *
 *   With async:
 *     POST /users → create user (5ms)
 *                 → RESPOND to client (total: 5ms!)   ← INSTANT
 *                 ↓ (background thread)
 *                 → write audit to MySQL (20ms)
 *                 → publish Kafka event (15ms)
 *
 * PSEUDOCODE — Thread pool:
 *   Pool starts with 5 threads (corePoolSize)
 *   Request 1 → thread-1 handles it
 *   Request 2 → thread-2 handles it
 *   ...
 *   Request 5 → thread-5 handles it
 *   Request 6 → all 5 busy → QUEUE the task (up to 100 tasks)
 *   Queue full + more requests? → create new threads (up to maxPoolSize=10)
 *   All 10 busy + queue full? → REJECT (throw RejectedExecutionException)
 *
 * PSEUDOCODE — Usage:
 *   @Async("taskExecutor")  // use THIS specific thread pool
 *   public CompletableFuture<Void> createAuditAsync(...) {
 *       // This runs on a DIFFERENT thread (e.g., "async-1")
 *       // The calling method's thread is FREE to return response
 *       userAuditRepository.save(audit);
 *       return CompletableFuture.completedFuture(null);
 *   }
 *
 * KEY: @EnableAsync must be on the main application class
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
