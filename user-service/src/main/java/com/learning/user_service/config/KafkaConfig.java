package com.learning.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.learning.common_library.constants.AppConstants.TOPIC_USER_EVENTS;

/**
 * Kafka Topic Configuration
 * Demonstrates: Event-driven architecture, Kafka topic creation
 *
 * CONCEPTS:
 * 1. NewTopic — declares a Kafka topic that Spring auto-creates on startup
 * 2. Partitions — parallel processing units (3 = 3 consumers can read in parallel)
 * 3. Replicas — fault tolerance (1 = single broker for dev, 3+ for production)
 *
 * Kafka must be running: brew services start kafka
 * Verify: kafka-topics --bootstrap-server localhost:9092 --list
 */
@Configuration
public class KafkaConfig {

    /**
     * Creates the "user-events" topic with 3 partitions.
     * user-service PUBLISHES to this topic when users are created/updated/deleted.
     * order-service CONSUMES from this topic to react to user changes.
     */
    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(TOPIC_USER_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
