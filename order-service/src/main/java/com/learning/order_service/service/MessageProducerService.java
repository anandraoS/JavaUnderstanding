package com.learning.order_service.service;

import com.learning.order_service.config.RabbitMQConfig;
import com.learning.order_service.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ Message Producer
 * Demonstrates: Publishing messages to RabbitMQ exchanges
 *
 * CONCEPTS:
 * 1. RabbitTemplate — Spring's helper to send messages to RabbitMQ
 * 2. convertAndSend(exchange, routingKey, message) — sends a message
 * 3. The message goes: RabbitTemplate → Exchange → Queue → Consumer
 *
 * This producer sends order notifications that OrderMessageListener consumes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Send order notification to RabbitMQ.
     *
     * PSEUDOCODE — Message flow:
     *   1. Build message map with order details
     *   2. rabbitTemplate.convertAndSend(exchange, routingKey, message)
     *   3. RabbitMQ receives message → routes to notification.queue
     *   4. OrderMessageListener picks up from notification.queue
     *   5. Listener processes (e.g., send email, push notification)
     *
     *   If RabbitMQ is DOWN → catch exception, log warning, continue
     *   Order creation should NOT fail just because notifications failed
     */
    public void sendOrderNotification(Order order) {
        try {
            log.info("Sending order notification to RabbitMQ: {}", order.getOrderNumber());

            Map<String, Object> message = new HashMap<>();
            message.put("orderId", order.getId());
            message.put("orderNumber", order.getOrderNumber());
            message.put("userId", order.getUserId());
            message.put("status", order.getStatus());
            message.put("totalAmount", order.getTotalAmount());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    message
            );

            log.info("Notification sent for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.warn("Failed to send RabbitMQ notification for order: {} — RabbitMQ may be unavailable: {}",
                    order.getOrderNumber(), e.getMessage());
        }
    }
}
