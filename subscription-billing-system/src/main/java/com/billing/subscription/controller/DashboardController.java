package com.billing.subscription.controller;

import com.billing.subscription.dto.response.ApiResponse;
import com.billing.subscription.service.InvoiceService;
import com.billing.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Reporting", description = "APIs for dashboard and reporting")
public class DashboardController {

    private final SubscriptionService subscriptionService;
    private final InvoiceService invoiceService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Returns key metrics for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("activeSubscriptions", subscriptionService.getActiveSubscriptionsCount());
        summary.put("paidInvoices", invoiceService.getPaidInvoicesCount());
        summary.put("overdueInvoices", invoiceService.getOverdueInvoicesCount());
        summary.put("lastUpdated", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success(summary, "Dashboard summary retrieved"));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue report", description = "Returns revenue statistics for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        
        Map<String, Object> report = new HashMap<>();
        BigDecimal totalRevenue = invoiceService.getRevenueBetween(start, end);
        
        report.put("startDate", start);
        report.put("endDate", end);
        report.put("totalRevenue", totalRevenue);
        report.put("paidInvoices", invoiceService.getPaidInvoicesCount());
        
        return ResponseEntity.ok(ApiResponse.success(report, "Revenue report retrieved"));
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "Get subscription metrics", description = "Returns subscription statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("activeSubscriptions", subscriptionService.getActiveSubscriptionsCount());
        metrics.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success(metrics, "Subscription metrics retrieved"));
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get invoice metrics", description = "Returns invoice statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInvoiceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("paidInvoices", invoiceService.getPaidInvoicesCount());
        metrics.put("overdueInvoices", invoiceService.getOverdueInvoicesCount());
        metrics.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success(metrics, "Invoice metrics retrieved"));
    }
}