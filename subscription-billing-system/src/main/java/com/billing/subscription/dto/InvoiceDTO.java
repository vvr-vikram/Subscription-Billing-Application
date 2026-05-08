package com.billing.subscription.dto;

import com.billing.subscription.entity.Invoice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    @Schema(description = "Invoice ID", example = "1")
    private Long id;

    @Schema(description = "Invoice number", example = "INV-2024-001")
    private String invoiceNumber;

    @Schema(description = "Subscription ID", example = "1")
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @Schema(description = "User ID", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "Subscription details")
    private SubscriptionDTO subscription;

    @Schema(description = "User details")
    private UserDTO user;

    @Schema(description = "Invoice status", example = "ISSUED")
    private Invoice.InvoiceStatus status;

    @Schema(description = "Invoice amount", example = "49.99")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Tax amount", example = "5.00")
    @NotNull(message = "Tax is required")
    private BigDecimal tax;

    @Schema(description = "Total amount", example = "54.99")
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @Schema(description = "Invoice date", example = "2024-01-01")
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    @Schema(description = "Due date", example = "2024-02-01")
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Schema(description = "Paid date", example = "2024-01-15")
    private LocalDate paidDate;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAt;
}