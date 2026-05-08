package com.billing.subscription.controller;

import com.billing.subscription.dto.InvoiceDTO;
import com.billing.subscription.dto.response.ApiResponse;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.Invoice;
import com.billing.subscription.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/v1/invoices")
@RequiredArgsConstructor
@Validated
@Tag(name = "Invoices", description = "APIs for managing invoices and billing")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice", description = "Creates a new invoice for a subscription")
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdInvoice, "Invoice created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Retrieves an invoice by its ID")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @GetMapping
    @Operation(summary = "Get all invoices", description = "Retrieves all invoices with pagination support")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getAllInvoices(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PagedResponse<InvoiceDTO> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/search")
    @Operation(summary = "Search invoices", description = "Searches invoices by number or user email")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> searchInvoices(
            @Parameter(description = "Search keyword") @RequestParam String search,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<InvoiceDTO> invoices = invoiceService.searchInvoices(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user invoices", description = "Retrieves all invoices for a specific user")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getUserInvoices(
            @PathVariable @Min(value = 1, message = "User ID must be greater than 0") Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<InvoiceDTO> invoices = invoiceService.getUserInvoices(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/subscription/{subscriptionId}")
    @Operation(summary = "Get subscription invoices", description = "Retrieves all invoices for a specific subscription")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getSubscriptionInvoices(
            @PathVariable @Min(value = 1, message = "Subscription ID must be greater than 0") Long subscriptionId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<InvoiceDTO> invoices = invoiceService.getSubscriptionInvoices(subscriptionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get invoices by status", description = "Retrieves invoices filtered by status")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getInvoicesByStatus(
            @PathVariable Invoice.InvoiceStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices", description = "Retrieves all overdue invoices")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getOverdueInvoices(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<InvoiceDTO> invoices = invoiceService.getOverdueInvoices(pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update invoice", description = "Updates invoice details (only draft invoices)")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateInvoice(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
            @Valid @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoice(id, invoiceDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedInvoice, "Invoice updated successfully"));
    }

    @PatchMapping("/{id}/mark-as-paid")
    @Operation(summary = "Mark invoice as paid", description = "Marks an invoice as paid")
    public ResponseEntity<ApiResponse<InvoiceDTO>> markAsPaid(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        InvoiceDTO invoice = invoiceService.markAsPaid(id);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice marked as paid"));
    }

    @PatchMapping("/{id}/mark-as-overdue")
    @Operation(summary = "Mark invoice as overdue", description = "Marks an invoice as overdue")
    public ResponseEntity<ApiResponse<InvoiceDTO>> markAsOverdue(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        InvoiceDTO invoice = invoiceService.markAsOverdue(id);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice marked as overdue"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice", description = "Deletes an invoice (only non-paid invoices)")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(
            @PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Invoice deleted successfully"));
    }

    @GetMapping("/stats/revenue")
    @Operation(summary = "Get revenue statistics", description = "Returns total revenue for a date range")
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenue(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal revenue = invoiceService.getRevenueBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }

    @GetMapping("/stats/paid-count")
    @Operation(summary = "Get paid invoices count", description = "Returns the count of paid invoices")
    public ResponseEntity<ApiResponse<Long>> getPaidInvoicesCount() {
        long count = invoiceService.getPaidInvoicesCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/stats/overdue-count")
    @Operation(summary = "Get overdue invoices count", description = "Returns the count of overdue invoices")
    public ResponseEntity<ApiResponse<Long>> getOverdueInvoicesCount() {
        long count = invoiceService.getOverdueInvoicesCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}