package com.learning.common_library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Data Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String orderNumber;

    @NotNull(message = "Order items are required")
    private List<OrderItemDTO> items;

    @NotNull(message = "Total amount is required")
    @Min(value = 0, message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private String status; // PENDING, PROCESSING, COMPLETED, CANCELLED
    private String paymentMethod;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
}

