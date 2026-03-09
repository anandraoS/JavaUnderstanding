package com.learning.order_service.controller;

import com.learning.common_library.dto.ApiResponse;
import com.learning.common_library.dto.OrderDTO;
import com.learning.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Order REST Controller
 * Demonstrates: RESTful API, Circuit breaker integration
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        log.info("REST: Creating order for user: {}", orderDTO.getUserId());
        OrderDTO created = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        log.info("REST: Getting order by ID: {}", id);
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("REST: Getting order by number: {}", orderNumber);
        OrderDTO order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST: Getting orders for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST: Getting all orders");
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        log.info("REST: Updating order status: {} to {}", id, status);
        OrderDTO updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        log.info("REST: Cancelling order: {}", id);
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
    }
}

