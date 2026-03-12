package com.learning.order_service.service;

import com.learning.order_service.config.RabbitMQConfig;
import com.learning.order_service.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ Message Producer
 * Demonstrates: Message broker, Async communication, Conditional beans
 *
 * RabbitTemplate is injected as Optional so the service starts even
 * when RabbitMQ is not available locally (local dev mode).
 */
@Service
@Slf4j
public class MessageProducerService {

    // Optional injection — null when RabbitAutoConfiguration is excluded
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageProducerService(@Autowired(required = false) RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderNotification(Order order) {
        if (rabbitTemplate == null) {
            log.warn("RabbitMQ not configured — skipping notification for order: {}", order.getOrderNumber());
            return;
        }

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
    }
}
