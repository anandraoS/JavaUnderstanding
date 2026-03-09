package com.learning.common_library.constants;

/**
 * Application-wide constants
 * Demonstrates: Constants management, Best practices
 */
public class AppConstants {

    // API Endpoints
    public static final String API_VERSION = "/api/v1";

    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";

    // Order Status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    // Event Types
    public static final String EVENT_ORDER_CREATED = "ORDER_CREATED";
    public static final String EVENT_ORDER_UPDATED = "ORDER_UPDATED";
    public static final String EVENT_ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String EVENT_USER_CREATED = "USER_CREATED";
    public static final String EVENT_USER_UPDATED = "USER_UPDATED";
    public static final String EVENT_USER_DELETED = "USER_DELETED";

    // Kafka Topics
    public static final String TOPIC_ORDER_EVENTS = "order-events";
    public static final String TOPIC_USER_EVENTS = "user-events";

    // Cache Names
    public static final String CACHE_USERS = "users";
    public static final String CACHE_ORDERS = "orders";

    // Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    private AppConstants() {
        throw new IllegalStateException("Constants class");
    }
}

