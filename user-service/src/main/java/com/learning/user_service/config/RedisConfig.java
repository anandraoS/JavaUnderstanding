package com.learning.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * ═══════════════════════════════════════════════════════════════════
 * REDIS CACHE CONFIGURATION (with In-Memory Fallback)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Distributed caching with Redis, Graceful fallback
 *
 * CONCEPT — WHY DISTRIBUTED CACHING?
 *   Local cache (HashMap): fast, but each server instance has its OWN cache
 *     Server-1 caches user 42 → Server-2 doesn't have it → cache miss
 *     Server-1 updates user 42 → Server-2 still has OLD data → stale!
 *
 *   Redis cache: ALL server instances share ONE cache
 *     Server-1 caches user 42 → Redis stores it
 *     Server-2 reads user 42 → Redis returns it (cache hit!)
 *     Server-1 updates user 42 → Redis updated → Server-2 gets new data
 *
 * CONCEPT — SERIALIZATION:
 *   Java objects can't be stored in Redis directly.
 *   We serialize them to JSON (GenericJackson2JsonRedisSerializer).
 *   Redis stores: "users::42" → '{"id":42,"username":"john","email":"j@x.com"}'
 *
 * CONCEPT — TTL (Time-To-Live):
 *   Cached entries auto-expire after 10 minutes.
 *   Why? Data might change in the database, and we don't want stale cache.
 *   After TTL expires → next request goes to DB → cache refreshed.
 *
 * CONCEPT — FALLBACK:
 *   If Redis is DOWN, we fallback to ConcurrentMapCacheManager (in-memory).
 *   The app still works, just without distributed caching.
 *
 * Redis must be running: brew services start redis
 * Verify: redis-cli ping → PONG
 */
@Configuration
@Slf4j
public class RedisConfig {

    /**
     * RedisTemplate for direct Redis operations.
     * Use this when you need fine-grained control beyond @Cacheable.
     *
     * PSEUDOCODE — RedisTemplate operations:
     *   redisTemplate.opsForValue().set("key", value)     → SET key value
     *   redisTemplate.opsForValue().get("key")            → GET key
     *   redisTemplate.opsForHash().put("hash", "field", v)→ HSET hash field v
     *   redisTemplate.opsForList().leftPush("list", v)    → LPUSH list v
     *   redisTemplate.delete("key")                       → DEL key
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * RedisCacheManager — powers @Cacheable, @CachePut, @CacheEvict annotations.
     * All cached entries use JSON serialization and expire after 10 minutes.
     *
     * PSEUDOCODE — How @Cacheable works with this manager:
     *   @Cacheable(value = "users", key = "#id")
     *   getUserById(42)
     *     → CacheManager.getCache("users").get("42")
     *     → Redis: GET "users::42"
     *     → MISS → execute method → save result to Redis
     *     → HIT → return cached value (skip method execution!)
     *
     * If Redis connection fails, falls back to in-memory cache automatically.
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        try {
            // Test Redis connection
            connectionFactory.getConnection().ping();

            ObjectMapper objectMapper = new ObjectMapper();
            // Register JavaTimeModule so LocalDateTime and other java.time types serialize/deserialize
            objectMapper.registerModule(new JavaTimeModule());
            // Write dates as ISO-8601 strings, not timestamps
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(serializer))
                    .disableCachingNullValues();

            log.info("✅ Redis is available — using RedisCacheManager");
            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(cacheConfig)
                    .build();
        } catch (Exception e) {
            log.warn("⚠️ Redis is unavailable — falling back to in-memory ConcurrentMapCacheManager: {}", e.getMessage());
            return new ConcurrentMapCacheManager("users");
        }
    }
}
