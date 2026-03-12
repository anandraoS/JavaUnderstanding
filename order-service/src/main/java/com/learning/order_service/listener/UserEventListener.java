package com.learning.order_service.listener;

import com.learning.common_library.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Listener
 * Demonstrates: Consuming events from Kafka topics
 *
 * CONCEPTS:
 * 1. @KafkaListener — subscribes to a Kafka topic and receives messages
 * 2. groupId — consumer group (multiple instances share the load)
 * 3. Event-driven: this listener reacts to events published by user-service
 *
 * Flow: UserService.publishUserEvent() → Kafka "user-events" topic → THIS LISTENER
 *
 * View topics/messages: Use Kafka UI at http://localhost:9093
 * Or CLI: kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events
 */
@Component
@Slf4j
public class UserEventListener {

    @KafkaListener(topics = "user-events", groupId = "order-service-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {} for user: {}", event.getEventType(), event.getUsername());

        switch (event.getEventType()) {
            case "USER_CREATED":
                log.info("New user registered: {} — can pre-create order preferences", event.getUsername());
                break;
            case "USER_UPDATED":
                log.info("User updated: {} — may need to update cached user data in orders", event.getUsername());
                break;
            case "USER_DELETED":
                log.info("User deleted: {} — should cancel pending orders for this user", event.getUsername());
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
