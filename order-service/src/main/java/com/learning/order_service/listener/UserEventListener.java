package com.learning.order_service.listener;

import com.learning.common_library.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * ═══════════════════════════════════════════════════════════════════
 * KAFKA EVENT LISTENER — Consumes User Events
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Event-driven architecture, Kafka consumer, Cross-service communication
 *
 * CONCEPT — EVENT-DRIVEN ARCHITECTURE:
 *   Traditional: order-service CALLS user-service directly (HTTP)
 *   Event-driven: order-service LISTENS for events from user-service
 *
 *   Benefits:
 *   → Services are DECOUPLED (order-service doesn't call user-service)
 *   → Services can be DOWN independently (events wait in Kafka)
 *   → Easy to add NEW consumers (e.g., notification-service just subscribes)
 *
 * PSEUDOCODE — How this works:
 *   1. User creates account → UserService.publishUserEvent("USER_CREATED")
 *   2. Event goes to Kafka topic "user-events"
 *   3. Kafka stores event permanently (configurable retention)
 *   4. THIS LISTENER picks up the event automatically
 *   5. We react: e.g., pre-create order preferences, update cached data
 *
 * CONCEPT — Consumer Group:
 *   groupId = "order-service-group"
 *   If you run 3 instances of order-service:
 *     → Kafka assigns 1 partition to each instance
 *     → Each event is processed by EXACTLY ONE instance
 *     → No duplicate processing!
 *
 * @ConditionalOnProperty ensures this bean is only created when Kafka is configured
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = false)
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
