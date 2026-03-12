package com.learning.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Demonstrates: Message queue configuration, Exchange, Queue, Binding
 *
 * CONCEPTS:
 * 1. Queue — where messages wait to be consumed (like a mailbox)
 * 2. Exchange — routes messages to queues based on routing keys
 * 3. Binding — connects an exchange to a queue with a routing key
 * 4. DirectExchange — routes message to queue where routing key matches exactly
 *
 * Flow: Producer → Exchange → (routing key) → Queue → Consumer
 *
 * RabbitMQ must be running: brew services start rabbitmq
 * Management UI: http://localhost:15672 (guest/guest)
 */
@Configuration
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_ROUTING_KEY = "order.routing.key";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.routing.key";

    // ─── Queues ───────────────────────────────────────────────────────────────
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);  // durable = survives broker restart
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // ─── Exchanges ────────────────────────────────────────────────────────────
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    // ─── Bindings (connect exchanges to queues) ───────────────────────────────
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    // ─── Message Converter (Java objects ↔ JSON) ──────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
