package com.billing.subscription.dto;

import com.billing.subscription.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "john.doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @Schema(description = "User email address", example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User role", example = "ADMIN")
    private User.UserRole role;

    @Schema(description = "User status", example = "ACTIVE")
    private User.UserStatus status;

    @Schema(description = "Is user active", example = "true")
    private Boolean active;

    @Schema(description = "User description")
    private String description;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAt;

	@Schema(description = "getpassword", example = "password123")
	private String password;
}