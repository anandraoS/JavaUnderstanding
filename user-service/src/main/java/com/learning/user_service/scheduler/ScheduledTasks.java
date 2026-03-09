package com.learning.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Scheduled Tasks
 * Demonstrates: @Scheduled annotation, Cron expressions, Task execution
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final CacheManager cacheManager;

    // Run every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    public void clearCache() {
        log.info("Scheduled task: Clearing cache");
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        log.info("Cache cleared successfully");
    }

    // Run every hour at the top of the hour
    @Scheduled(cron = "0 0 * * * *")
    public void generateReport() {
        log.info("Scheduled task: Generating hourly report");
        // Report generation logic here
        log.info("Hourly report generated");
    }

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCleanup() {
        log.info("Scheduled task: Daily cleanup");
        // Cleanup logic here
        log.info("Daily cleanup completed");
    }

    // Fixed rate - runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void healthCheck() {
        log.debug("Scheduled task: Health check");
        // Health check logic
    }
}

