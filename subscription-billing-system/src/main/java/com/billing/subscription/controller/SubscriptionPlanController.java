package com.billing.subscription.controller;

import com.billing.subscription.dto.SubscriptionPlanDTO;
import com.billing.subscription.dto.response.ApiResponse;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.SubscriptionPlan;
import com.billing.subscription.service.SubscriptionPlanService;
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
@RequestMapping("/v1/plans")
@RequiredArgsConstructor
@Validated
@Tag(name = "Subscription Plans", description = "APIs for managing subscription plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    @PostMapping
    @Operation(summary = "Create a new subscription plan", description = "Creates a new subscription plan with pricing and features")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> createPlan(@Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanDTO createdPlan = planService.createPlan(planDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdPlan, "Subscription plan created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by ID", description = "Retrieves a subscription plan by its ID")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> getPlanById(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        SubscriptionPlanDTO plan = planService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @GetMapping
    @Operation(summary = "Get all plans", description = "Retrieves all subscription plans with pagination support")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionPlanDTO>>> getAllPlans(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<SubscriptionPlanDTO> plans = planService.getAllPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/search")
    @Operation(summary = "Search plans", description = "Searches subscription plans by name or description")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionPlanDTO>>> searchPlans(
            @Parameter(description = "Search keyword") @RequestParam String search,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionPlanDTO> plans = planService.searchPlans(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active plans", description = "Retrieves all active subscription plans")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionPlanDTO>>> getActivePlans(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionPlanDTO> plans = planService.getActivePlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured plans", description = "Retrieves all featured subscription plans")
    public ResponseEntity<ApiResponse<PagedResponse<SubscriptionPlanDTO>>> getFeaturedPlans(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SubscriptionPlanDTO> plans = planService.getFeaturedPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update plan", description = "Updates subscription plan details")
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> updatePlan(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanDTO updatedPlan = planService.updatePlan(id, planDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPlan, "Plan updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete plan", description = "Deletes a subscription plan by its ID")
    public ResponseEntity<ApiResponse<Void>> deletePlan(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Plan deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change plan status", description = "Changes the status of a subscription plan")
    public ResponseEntity<ApiResponse<Void>> changePlanStatus(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @RequestParam SubscriptionPlan.PlanStatus status) {
        planService.changePlanStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Plan status changed successfully"));
    }
}