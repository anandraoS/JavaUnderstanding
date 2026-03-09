package com.learning.order_service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RabbitMQ Message Consumer
 * Demonstrates: Message queue consumption
 */
@Component
@Slf4j
public class OrderMessageListener {

    @RabbitListener(queues = "order.queue")
    public void handleOrderMessage(Map<String, Object> message) {
        log.info("Received order message from RabbitMQ: {}", message);

        // Process the order message
        String orderNumber = (String) message.get("orderNumber");
        String status = (String) message.get("status");

        log.info("Processing order: {} with status: {}", orderNumber, status);

        // Add your business logic here
    }
}

