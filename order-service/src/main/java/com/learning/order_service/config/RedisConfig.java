package com.learning.order_service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
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
 * REDIS CACHE CONFIGURATION for Order Service (with In-Memory Fallback)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Distributed caching with Redis
 *
 * CONCEPT — Cache strategy for orders:
 *   GET /orders/1 → first time: query DB (50ms) → cache in Redis
 *   GET /orders/1 → second time: read Redis (0.1ms) → 500x faster!
 *   PUT /orders/1/status → update DB → @CachePut updates cache
 *   DELETE /orders/1 → cancel order → @CacheEvict removes from cache
 *
 * Redis must be running: brew services start redis
 */
@Configuration
@Slf4j
public class RedisConfig {

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

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        try {
            connectionFactory.getConnection().ping();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Configure type validator to prevent classloader mismatch errors
            // when deserializing cached DTOs from common-library
            PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Object.class)
                    .allowIfSubType("com.learning.")
                    .allowIfSubType("java.")
                    .allowIfSubType("org.springframework.data.domain.")
                    .build();

            objectMapper.activateDefaultTyping(
                    typeValidator,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
            );

            GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(serializer))
                    .disableCachingNullValues();

            log.info("✅ Redis is available — using RedisCacheManager for order-service");
            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(cacheConfig)
                    .build();
        } catch (Exception e) {
            log.warn("⚠️ Redis is unavailable — falling back to in-memory ConcurrentMapCacheManager: {}", e.getMessage());
            return new ConcurrentMapCacheManager("orders");
        }
    }
}

