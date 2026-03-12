package com.learning.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.learning.common_library.constants.AppConstants.TOPIC_ORDER_EVENTS;

/**
 * Kafka Topic Configuration for Order Service
 * Demonstrates: Kafka topic creation, Event-driven architecture
 *
 * CONCEPTS:
 * 1. NewTopic — Spring auto-creates this topic in Kafka broker on startup
 * 2. Partitions — 3 partitions allow 3 consumers to read in parallel
 * 3. Replicas — 1 for local dev (use 3+ in production for fault tolerance)
 *
 * order-service PUBLISHES to "order-events" topic.
 * order-service CONSUMES from "user-events" topic (see UserEventListener.java).
 *
 * Kafka must be running: brew services start kafka
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(TOPIC_ORDER_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

