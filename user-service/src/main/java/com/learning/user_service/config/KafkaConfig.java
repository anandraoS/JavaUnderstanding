package com.learning.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.learning.common_library.constants.AppConstants.TOPIC_USER_EVENTS;

/**
 * Kafka Configuration
 * Demonstrates: Event-driven architecture, Message broker configuration
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(TOPIC_USER_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

