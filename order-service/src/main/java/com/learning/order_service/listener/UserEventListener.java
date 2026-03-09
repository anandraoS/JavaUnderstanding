package com.learning.order_service.listener;

import com.learning.common_library.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Listener
 * Demonstrates: Event consumption, Event-driven architecture
 */
@Component
@Slf4j
public class UserEventListener {

    @KafkaListener(topics = "user-events", groupId = "order-service-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {} for user: {}", event.getEventType(), event.getUsername());

        // Handle different event types
        switch (event.getEventType()) {
            case "USER_CREATED":
                log.info("New user registered: {}", event.getUsername());
                break;
            case "USER_UPDATED":
                log.info("User updated: {}", event.getUsername());
                break;
            case "USER_DELETED":
                log.info("User deleted: {}", event.getUsername());
                // Handle cleanup logic
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}

