package com.billing.subscription.repository;

import com.billing.subscription.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findBySubscriptionNumber(String subscriptionNumber);
    Page<Subscription> findByUserId(Long userId, Pageable pageable);
    Page<Subscription> findByStatus(Subscription.SubscriptionStatus status, Pageable pageable);
    Page<Subscription> findByBillingCycle(Subscription.BillingCycle billingCycle, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE " +
            "LOWER(s.subscriptionNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.plan.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Subscription> searchSubscriptions(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate = :date AND s.status = 'ACTIVE'")
    Page<Subscription> findByNextBillingDate(@Param("date") LocalDate date, Pageable pageable);

    long countByStatus(Subscription.SubscriptionStatus status);
}