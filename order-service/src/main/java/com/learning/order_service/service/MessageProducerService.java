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
 * Demonstrates: Message broker, Async communication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderNotification(Order order) {
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

