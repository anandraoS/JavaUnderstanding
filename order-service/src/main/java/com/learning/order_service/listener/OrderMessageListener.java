package com.learning.order_service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RabbitMQ Message Consumer
 * Demonstrates: Consuming messages from RabbitMQ queues
 *
 * CONCEPTS:
 * 1. @RabbitListener — listens on a specific queue for incoming messages
 * 2. When a message arrives in "order.queue", this method is called automatically
 * 3. Spring deserializes the JSON message into a Map
 *
 * Flow: MessageProducerService → Exchange → Queue → THIS LISTENER
 *
 * View messages in RabbitMQ UI: http://localhost:15672 → Queues tab
 */
@Component
@Slf4j
public class OrderMessageListener {

    @RabbitListener(queues = "order.queue")
    public void handleOrderMessage(Map<String, Object> message) {
        log.info("Received order message from RabbitMQ: {}", message);

        String orderNumber = (String) message.get("orderNumber");
        String status = (String) message.get("status");

        log.info("Processing order: {} with status: {}", orderNumber, status);
        // Add your business logic here (e.g., send email, update inventory)
    }
}
