package com.learning.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Event for async communication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long userId;
    private String username;
    private String email;
    private String eventType; // USER_CREATED, USER_UPDATED, USER_DELETED
    private LocalDateTime timestamp;
}

