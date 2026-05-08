package com.billing.subscription.repository;

import com.billing.subscription.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByName(String name);
    Page<SubscriptionPlan> findByStatus(SubscriptionPlan.PlanStatus status, Pageable pageable);
    Page<SubscriptionPlan> findByFeatured(Boolean featured, Pageable pageable);

    @Query("SELECT p FROM SubscriptionPlan p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<SubscriptionPlan> searchPlans(@Param("search") String search, Pageable pageable);

    boolean existsByName(String name);
}