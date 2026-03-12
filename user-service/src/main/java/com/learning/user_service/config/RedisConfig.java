package com.learning.user_service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
 * Redis Cache Configuration
 * Demonstrates: Caching strategies with Redis, Conditional beans, Fallback patterns
 *
 * - When Redis IS available: uses RedisCacheManager for distributed caching
 * - When Redis IS NOT available: falls back to in-memory ConcurrentMapCacheManager
 *   (service still starts without Redis locally)
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate — only created when RedisConnectionFactory exists (Redis running).
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
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
     * Redis-based CacheManager — used when Redis is available.
     */
    @Bean
    @Primary
    @ConditionalOnBean(RedisConnectionFactory.class)
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * Fallback in-memory CacheManager — used when Redis is NOT available (local dev).
     * This lets @Cacheable / @CacheEvict / @CachePut still work without Redis.
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager fallbackCacheManager() {
        return new ConcurrentMapCacheManager("users", "orders");
    }
}
