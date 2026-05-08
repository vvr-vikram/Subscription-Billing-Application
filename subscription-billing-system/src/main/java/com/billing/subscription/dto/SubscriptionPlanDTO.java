package com.billing.subscription.dto;

import com.billing.subscription.entity.SubscriptionPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanDTO {

    @Schema(description = "Plan ID", example = "1")
    private Long id;

    @Schema(description = "Plan name", example = "Professional Plan")
    @NotBlank(message = "Plan name is required")
    private String name;

    @Schema(description = "Plan description")
    private String description;

    @Schema(description = "Monthly price", example = "49.99")
    @NotNull(message = "Monthly price is required")
    @Positive(message = "Monthly price must be positive")
    private BigDecimal monthlyPrice;

    @Schema(description = "Yearly price", example = "499.99")
    @NotNull(message = "Yearly price is required")
    @Positive(message = "Yearly price must be positive")
    private BigDecimal yearlyPrice;

    @Schema(description = "Maximum users", example = "50")
    @NotNull(message = "Max users is required")
    @Positive(message = "Max users must be positive")
    private Integer maxUsers;

    @Schema(description = "Maximum projects", example = "100")
    @NotNull(message = "Max projects is required")
    @Positive(message = "Max projects must be positive")
    private Integer maxProjects;

    @Schema(description = "Storage in GB", example = "1000")
    @NotNull(message = "Storage is required")
    @Positive(message = "Storage must be positive")
    private Integer storageGB;

    @Schema(description = "Plan status", example = "ACTIVE")
    private SubscriptionPlan.PlanStatus status;

    @Schema(description = "Is featured plan", example = "true")
    private Boolean featured;

    @Schema(description = "Features list")
    private String features;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAt;
}