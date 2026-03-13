package com.learning.order_service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════
 * RABBITMQ MESSAGE CONSUMER — Listens on Order Queue
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: RabbitMQ consumer, Message queue pattern
 *
 * CONCEPT — DIFFERENCE FROM KAFKA LISTENER:
 *   Kafka listener: reads from a LOG (events persist, can replay)
 *   RabbitMQ listener: reads from a QUEUE (message consumed = gone)
 *
 * CONCEPT — HOW @RabbitListener WORKS:
 *   1. Spring connects to RabbitMQ broker on startup
 *   2. Subscribes to "order.queue"
 *   3. When a message arrives → Spring deserializes JSON → calls this method
 *   4. After successful processing → message is ACKed (acknowledged)
 *   5. If method throws exception → message is NACKed → requeued or dead-lettered
 *
 * PSEUDOCODE — Message journey:
 *   MessageProducerService.sendOrderNotification()
 *     → rabbitTemplate.convertAndSend("notification.exchange", "notification.routing.key", msg)
 *     → RabbitMQ routes message to notification.queue
 *     → THIS METHOD is called with the message
 *     → Process: send email, update inventory, etc.
 *
 * @ConditionalOnProperty ensures listener only activates when RabbitMQ is configured
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.host", matchIfMissing = false)
public class OrderMessageListener {

    @RabbitListener(queues = "order.queue")
    public void handleOrderMessage(Map<String, Object> message) {
        log.info("Received order message from RabbitMQ: {}", message);

        String orderNumber = (String) message.get("orderNumber");
        String status = (String) message.get("status");

        log.info("Processing order notification: {} with status: {}", orderNumber, status);
        // In production: send email, push notification, update inventory, etc.
    }
}
