package com.billing.subscription.controller;

import com.billing.subscription.dto.UserDTO;
import com.billing.subscription.dto.response.ApiResponse;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.User;
import com.billing.subscription.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "APIs for managing users and roles")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "User created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination support")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Searches users by keyword in username, email, or name")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> searchUsers(
            @Parameter(description = "Search keyword") @RequestParam String search,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<UserDTO> users = userService.searchUsers(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users filtered by their role")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getUsersByRole(
            @PathVariable User.UserRole role,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<UserDTO> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieves users filtered by their status")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getUsersByStatus(
            @PathVariable User.UserStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<UserDTO> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change user status", description = "Changes the status of a user")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @RequestParam User.UserStatus status) {
        userService.changeUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "User status changed successfully"));
    }
}