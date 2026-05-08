package com.billing.subscription.controller;

import com.billing.subscription.dto.SubscriptionDTO;
import com.billing.subscription.dto.response.ApiResponse;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.Subscription;
import com.billing.subscription.service.SubscriptionService;
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
@RequestMapping("/v1/subscriptions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Subscriptions", description = "APIs for managing customer subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Create a new subscription", description = "Creates a new subscription for a user")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO createdSubscription = subscriptionService.createSubscription(subscriptionDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdSubscription, "Subscription created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID", description = "Retrieves a subscription by its ID")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> getSubscriptionById(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }

    @GetMapping
    @Operation(summary = "Get all subscriptions", description = "Retrieves all subscriptions with pagination support")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionDTO>>> getAllSubscriptions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @GetMapping("/search")
    @Operation(summary = "Search subscriptions", description = "Searches subscriptions by number or user email")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionDTO>>> searchSubscriptions(
            @Parameter(description = "Search keyword") @RequestParam String search,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionDTO> subscriptions = subscriptionService.searchSubscriptions(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user subscriptions", description = "Retrieves all subscriptions for a specific user")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionDTO>>> getUserSubscriptions(
            @PathVariable @Min(value = 1, message = "User ID must be greater than 0") Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get subscriptions by status", description = "Retrieves subscriptions filtered by status")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionDTO>>> getSubscriptionsByStatus(
            @PathVariable Subscription.SubscriptionStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription", description = "Updates subscription details")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> updateSubscription(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedSubscription, "Subscription updated successfully"));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancels an active subscription")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription cancelled successfully"));
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend subscription", description = "Suspends an active subscription")
    public ResponseEntity<ApiResponse<Void>> suspendSubscription(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        subscriptionService.suspendSubscription(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription suspended successfully"));
    }

    @PatchMapping("/{id}/change-plan")
    @Operation(summary = "Change subscription plan", description = "Changes the plan for an existing subscription")
    public ResponseEntity<ApiResponse<Void>> changeSubscriptionPlan(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @RequestParam @Min(value = 1, message = "Plan ID must be greater than 0") Long newPlanId) {
        subscriptionService.changeSubscriptionPlan(id, newPlanId);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription plan changed successfully"));
    }

    @GetMapping("/stats/active-count")
    @Operation(summary = "Get active subscriptions count", description = "Returns the count of active subscriptions")
    public ResponseEntity<ApiResponse<Long>> getActiveSubscriptionsCount() {
        long count = subscriptionService.getActiveSubscriptionsCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}