package com.learning.order_service.service;

import com.learning.common_library.constants.AppConstants;
import com.learning.common_library.dto.OrderDTO;
import com.learning.common_library.dto.OrderItemDTO;
import com.learning.common_library.event.OrderEvent;
import com.learning.common_library.exception.BusinessException;
import com.learning.common_library.exception.ResourceNotFoundException;
import com.learning.order_service.entity.Order;
import com.learning.order_service.entity.OrderItem;
import com.learning.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════════════
 * ORDER SERVICE — Business Logic with Resilience Patterns
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Circuit Breaker, Retry, Inter-service communication,
 *               Kafka events, RabbitMQ messages, Async processing
 *
 * CONCEPT — ORDER CREATION FLOW:
 *   1. Validate user exists (via WebClient → user-service, with circuit breaker)
 *   2. Calculate total from order items
 *   3. Save order to PostgreSQL
 *   4. Async: process order in background thread
 *   5. Async: publish event to Kafka (for analytics/audit)
 *   6. Async: send notification via RabbitMQ (for email/SMS)
 *
 * CONCEPT — WHY SO MANY MESSAGING SYSTEMS?
 *   This is a LEARNING project showing BOTH Kafka AND RabbitMQ:
 *   - Kafka: event log (persist events, replay, analytics)
 *   - RabbitMQ: task queue (send email, process payment, one-time tasks)
 *   In production, you'd typically choose ONE based on your use case.
 */
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final MessageProducerService messageProducerService;

    @Autowired(required = false)
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository,
                        WebClient.Builder webClientBuilder,
                        MessageProducerService messageProducerService) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.messageProducerService = messageProducerService;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for user: {}", orderDTO.getUserId());

        // Validate user exists (with circuit breaker)
        validateUser(orderDTO.getUserId());

        // Calculate total
        BigDecimal total = orderDTO.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(orderDTO.getUserId())
                .totalAmount(total)
                .status(AppConstants.ORDER_STATUS_PENDING)
                .paymentMethod(orderDTO.getPaymentMethod())
                .build();

        // Add items
        Order finalOrder = order;
        orderDTO.getItems().forEach(itemDTO -> {
            OrderItem item = OrderItem.builder()
                    .productName(itemDTO.getProductName())
                    .quantity(itemDTO.getQuantity())
                    .price(itemDTO.getPrice())
                    .subtotal(itemDTO.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                    .build();
            finalOrder.addItem(item);
        });

        order = orderRepository.save(order);
        log.info("Order created with number: {}", order.getOrderNumber());

        // Async processing
        processOrderAsync(order);

        // Publish event to Kafka (async, fire-and-forget)
        publishOrderEvent(order, AppConstants.EVENT_ORDER_CREATED);

        // Send to RabbitMQ for notification (resilient)
        try {
            messageProducerService.sendOrderNotification(order);
        } catch (Exception e) {
            log.warn("Failed to send RabbitMQ notification for order: {} — RabbitMQ may be unavailable: {}",
                    order.getOrderNumber(), e.getMessage());
        }

        return mapToDTO(order);
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    @Retry(name = "userService")
    public void validateUser(Long userId) {
        log.info("Validating user: {}", userId);

        // user-service returns ApiResponse<UserDTO> wrapper, not raw UserDTO
        // We only need to confirm the user exists, so we check for a successful response
        Boolean userExists = webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/" + userId)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .block();

        if (userExists == null || !userExists) {
            throw new BusinessException("User not found", "USER_NOT_FOUND");
        }

        log.info("User validated successfully for userId: {}", userId);
    }

    public void validateUserFallback(Long userId, Exception ex) {
        log.warn("User service unavailable, using fallback for user: {}", userId);
        // In real scenario, you might check cache or allow order with pending validation
        throw new BusinessException("User service is temporarily unavailable", "SERVICE_UNAVAILABLE");
    }

    @Cacheable(value = AppConstants.CACHE_ORDERS, key = "#id")
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.info("Fetching order by ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderByNumber(String orderNumber) {
        log.info("Fetching order by number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId, pageable).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.info("Fetching all orders");
        return orderRepository.findAll(pageable).map(this::mapToDTO);
    }

    @CacheEvict(value = AppConstants.CACHE_ORDERS, key = "#id")
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        log.info("Updating order status: {} to {}", id, status);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setStatus(status);
        order = orderRepository.save(order);

        publishOrderEvent(order, AppConstants.EVENT_ORDER_UPDATED);

        return mapToDTO(order);
    }

    @CacheEvict(value = AppConstants.CACHE_ORDERS, key = "#id")
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancelling order: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (AppConstants.ORDER_STATUS_COMPLETED.equals(order.getStatus())) {
            throw new BusinessException("Cannot cancel completed order", "INVALID_OPERATION");
        }

        order.setStatus(AppConstants.ORDER_STATUS_CANCELLED);
        orderRepository.save(order);

        publishOrderEvent(order, AppConstants.EVENT_ORDER_CANCELLED);
    }

    @Async
    public CompletableFuture<Void> processOrderAsync(Order order) {
        log.info("Processing order asynchronously: {}", order.getOrderNumber());

        try {
            // Simulate processing time
            Thread.sleep(2000);

            // Update status
            order.setStatus(AppConstants.ORDER_STATUS_PROCESSING);
            orderRepository.save(order);

            log.info("Order processed: {}", order.getOrderNumber());
        } catch (InterruptedException e) {
            log.error("Error processing order", e);
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * PSEUDOCODE — Kafka event publishing:
     *   Build event object → send to "order-events" topic
     *   Other services (e.g., notification-service, analytics) consume this event
     *   If Kafka is unavailable → log warning, don't crash
     */
    private void publishOrderEvent(Order order, String eventType) {
        if (kafkaTemplate == null) {
            log.warn("KafkaTemplate not available — skipping event: {} for order: {}", eventType, order.getOrderNumber());
            return;
        }
        try {
            OrderEvent event = OrderEvent.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .eventType(eventType)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(AppConstants.TOPIC_ORDER_EVENTS, event);
            log.info("Published Kafka event: {} for order: {}", eventType, order.getOrderNumber());
        } catch (Exception e) {
            log.warn("Failed to publish Kafka event: {} for order: {} — Kafka may be unavailable: {}",
                    eventType, order.getOrderNumber(), e.getMessage());
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .id(item.getId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

