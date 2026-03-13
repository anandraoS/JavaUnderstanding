package com.learning.common_library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════
 * USER DATA TRANSFER OBJECT (DTO)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: DTOs, Validation annotations, Lombok
 *
 * CONCEPT — WHY USE DTOs?
 *   Entity (User.java) = maps to DB table, has password hash, internal fields
 *   DTO (UserDTO.java) = what the CLIENT sees, no sensitive data
 *
 *   Without DTO: return user entity → password hash EXPOSED to client!
 *   With DTO:    convert entity → DTO (exclude password) → return to client
 *
 * CONCEPT — VALIDATION ANNOTATIONS:
 *   @NotBlank  → field must not be null or empty (after trimming)
 *   @Email     → must match email format (xxx@yyy.zzz)
 *   @Size      → string length must be within range
 *   @Min/@Max  → numeric value must be within range
 *
 *   These are checked when @Valid is used on the controller parameter:
 *   createUser(@Valid @RequestBody UserDTO userDTO)
 *   → if validation fails → 400 Bad Request with error details
 *
 * CONCEPT — PASSWORD FIELD:
 *   Used only for registration (POST /api/v1/users)
 *   NOT included in GET responses (mapToDTO() doesn't set it)
 *   @Size validation only applies when password is provided
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String firstName;
    private String lastName;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

