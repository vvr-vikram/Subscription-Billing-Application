package com.billing.subscription.repository;

import com.billing.subscription.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    Page<Invoice> findBySubscriptionId(Long subscriptionId, Pageable pageable);
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.user.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Invoice> searchInvoices(@Param("search") String search, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :date AND i.status != 'PAID'")
    Page<Invoice> findOverdueInvoices(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND " +
            "i.paidDate >= :startDate AND i.paidDate <= :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    long countByStatus(Invoice.InvoiceStatus status);
}