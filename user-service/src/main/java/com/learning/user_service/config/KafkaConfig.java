package com.learning.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.learning.common_library.constants.AppConstants.TOPIC_USER_EVENTS;

/**
 * Kafka Configuration
 * Demonstrates: Event-driven architecture, Message broker configuration
 *
 * Topic creation is conditional so the service starts even when no
 * Kafka broker is running locally.
 * Set spring.kafka.enabled=true (or run with Docker Compose) to activate.
 */
@Configuration
public class KafkaConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.kafka.bootstrap-servers",
                           havingValue = "localhost:9092",
                           matchIfMissing = false)
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(TOPIC_USER_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
