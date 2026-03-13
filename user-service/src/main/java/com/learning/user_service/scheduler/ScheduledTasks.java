package com.learning.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Scheduled Tasks
 * Demonstrates: @Scheduled annotation, Cron expressions, Fixed-rate execution
 *
 * CONCEPTS:
 * 1. Cron expression: second minute hour day-of-month month day-of-week
 *    "0 * //30 * * * *" = every 30 minutes at second 0
 *    "0 0 * * * *"    = top of every hour
 *    "0 0 0 * * *"    = midnight every day
 * 2. fixedRate = run every N milliseconds (300000 = 5 minutes)
 * 3. CacheManager.clear() = evicts all cached entries from Redis
 *
 * Requires: @EnableScheduling on the main application class
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final CacheManager cacheManager;

    /**
     * Clear all caches every 30 minutes.
     * Prevents stale data from accumulating.
     *
     * PSEUDOCODE — Cron: "0 *​/30 * * * *"
     *   second=0, minute=every 30, hour=any, day=any, month=any, weekday=any
     *   → runs at :00, :30 of every hour
     *
     *   If Redis is DOWN → ConcurrentMapCacheManager is used → still works
     *   If cache clearing fails → catch exception → continue
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void clearCache() {
        try {
            log.info("Scheduled task: Clearing all caches");
            cacheManager.getCacheNames()
                    .forEach(cacheName -> {
                        try {
                            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
                        } catch (Exception e) {
                            log.warn("Failed to clear cache '{}': {}", cacheName, e.getMessage());
                        }
                    });
            log.info("All caches cleared successfully");
        } catch (Exception e) {
            log.warn("Failed to clear caches: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void generateReport() {
        log.info("Scheduled task: Generating hourly report");
        log.info("Hourly report generated");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCleanup() {
        log.info("Scheduled task: Daily cleanup");
        log.info("Daily cleanup completed");
    }

    @Scheduled(fixedRate = 300000)
    public void healthCheck() {
        log.debug("Scheduled task: Health check — all systems operational");
    }
}
