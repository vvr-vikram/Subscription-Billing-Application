package com.billing.subscription.dto;

import com.billing.subscription.entity.Subscription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {

    @Schema(description = "Subscription ID", example = "1")
    private Long id;

    @Schema(description = "Subscription number", example = "SUB-2024-001")
    private String subscriptionNumber;

    @Schema(description = "User ID", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "Plan ID", example = "1")
    @NotNull(message = "Plan ID is required")
    private Long planId;

    @Schema(description = "User details")
    private UserDTO user;

    @Schema(description = "Plan details")
    private SubscriptionPlanDTO plan;

    @Schema(description = "Subscription status", example = "ACTIVE")
    private Subscription.SubscriptionStatus status;

    @Schema(description = "Billing cycle", example = "MONTHLY")
    private Subscription.BillingCycle billingCycle;

    @Schema(description = "Current price", example = "49.99")
    private BigDecimal currentPrice;

    @Schema(description = "Start date", example = "2024-01-01")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Schema(description = "End date", example = "2024-12-31")
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Schema(description = "Next billing date", example = "2024-02-01")
    private LocalDate nextBillingDate;

    @Schema(description = "Auto renew enabled", example = "true")
    private Boolean autoRenew;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAt;
}