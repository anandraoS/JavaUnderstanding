package com.learning.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Event for async communication via Kafka
 * Demonstrates: Event-driven architecture, Message brokers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String eventType; // ORDER_CREATED, ORDER_UPDATED, ORDER_CANCELLED
    private LocalDateTime timestamp;
}

