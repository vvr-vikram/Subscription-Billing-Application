package com.billing.subscription.service;

import com.billing.subscription.dto.SubscriptionDTO;
import com.billing.subscription.dto.SubscriptionPlanDTO;
import com.billing.subscription.dto.UserDTO;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.Subscription;
import com.billing.subscription.entity.SubscriptionPlan;
import com.billing.subscription.entity.User;
import com.billing.subscription.exception.InvalidOperationException;
import com.billing.subscription.exception.ResourceNotFoundException;
import com.billing.subscription.repository.SubscriptionPlanRepository;
import com.billing.subscription.repository.SubscriptionRepository;
import com.billing.subscription.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final AuditService auditService;

    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        log.info("Creating new subscription for user ID: {}", subscriptionDTO.getUserId());

        User user = userRepository.findById(subscriptionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + subscriptionDTO.getUserId()));

        SubscriptionPlan plan = planRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + subscriptionDTO.getPlanId()));

        if (!plan.getStatus().equals(SubscriptionPlan.PlanStatus.ACTIVE)) {
            throw new InvalidOperationException("Cannot subscribe to inactive plan");
        }

        Subscription subscription = Subscription.builder()
                .subscriptionNumber("SUB-" + YearMonth.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .plan(plan)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .billingCycle(subscriptionDTO.getBillingCycle() != null ? subscriptionDTO.getBillingCycle() : Subscription.BillingCycle.MONTHLY)
                .currentPrice(subscriptionDTO.getBillingCycle() == Subscription.BillingCycle.YEARLY ? plan.getYearlyPrice() : plan.getMonthlyPrice())
                .startDate(subscriptionDTO.getStartDate() != null ? subscriptionDTO.getStartDate() : LocalDate.now())
                .endDate(subscriptionDTO.getEndDate())
                .nextBillingDate(subscriptionDTO.getNextBillingDate() != null ? subscriptionDTO.getNextBillingDate() : LocalDate.now().plusMonths(1))
                .autoRenew(subscriptionDTO.getAutoRenew() != null ? subscriptionDTO.getAutoRenew() : true)
                .notes(subscriptionDTO.getNotes())
                .createdBy("SYSTEM")
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription created successfully with ID: {}", savedSubscription.getId());
        auditService.log("Subscription", savedSubscription.getId(), "CREATE", "SYSTEM", null, null);

        return mapToDTO(savedSubscription);
    }

    public SubscriptionDTO getSubscriptionById(Long id) {
        log.info("Fetching subscription with ID: {}", id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));
        return mapToDTO(subscription);
    }

    public PagedResponse<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        log.info("Fetching all subscriptions");
        Page<Subscription> subscriptions = subscriptionRepository.findAll(pageable);
        return mapPageToDTO(subscriptions);
    }

    public PagedResponse<SubscriptionDTO> searchSubscriptions(String search, Pageable pageable) {
        log.info("Searching subscriptions with keyword: {}", search);
        Page<Subscription> subscriptions = subscriptionRepository.searchSubscriptions(search, pageable);
        return mapPageToDTO(subscriptions);
    }

    public PagedResponse<SubscriptionDTO> getUserSubscriptions(Long userId, Pageable pageable) {
        log.info("Fetching subscriptions for user ID: {}", userId);
        
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Page<Subscription> subscriptions = subscriptionRepository.findByUserId(userId, pageable);
        return mapPageToDTO(subscriptions);
    }

    public PagedResponse<SubscriptionDTO> getSubscriptionsByStatus(Subscription.SubscriptionStatus status, Pageable pageable) {
        log.info("Fetching subscriptions by status: {}", status);
        Page<Subscription> subscriptions = subscriptionRepository.findByStatus(status, pageable);
        return mapPageToDTO(subscriptions);
    }

    public SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        log.info("Updating subscription with ID: {}", id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));

        String oldValues = subscription.toString();

        if (subscriptionDTO.getEndDate() != null) {
            subscription.setEndDate(subscriptionDTO.getEndDate());
        }
        if (subscriptionDTO.getNextBillingDate() != null) {
            subscription.setNextBillingDate(subscriptionDTO.getNextBillingDate());
        }
        if (subscriptionDTO.getAutoRenew() != null) {
            subscription.setAutoRenew(subscriptionDTO.getAutoRenew());
        }
        if (subscriptionDTO.getNotes() != null) {
            subscription.setNotes(subscriptionDTO.getNotes());
        }

        subscription.setUpdatedBy("SYSTEM");
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription updated successfully with ID: {}", updatedSubscription.getId());
        auditService.log("Subscription", updatedSubscription.getId(), "UPDATE", "SYSTEM", oldValues, subscription.toString());

        return mapToDTO(updatedSubscription);
    }

    public void cancelSubscription(Long id) {
        log.info("Cancelling subscription with ID: {}", id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));

        if (subscription.getStatus() == Subscription.SubscriptionStatus.CANCELLED) {
            throw new InvalidOperationException("Subscription is already cancelled");
        }

        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setUpdatedBy("SYSTEM");
        subscriptionRepository.save(subscription);
        log.info("Subscription cancelled successfully");
        auditService.log("Subscription", id, "UPDATE", "SYSTEM", null, null);
    }

    public void suspendSubscription(Long id) {
        log.info("Suspending subscription with ID: {}", id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));

        subscription.setStatus(Subscription.SubscriptionStatus.SUSPENDED);
        subscription.setUpdatedBy("SYSTEM");
        subscriptionRepository.save(subscription);
        log.info("Subscription suspended successfully");
        auditService.log("Subscription", id, "UPDATE", "SYSTEM", null, null);
    }

    public void changeSubscriptionPlan(Long subscriptionId, Long newPlanId) {
        log.info("Changing subscription plan to ID: {} for subscription ID: {}", newPlanId, subscriptionId);
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        SubscriptionPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + newPlanId));

        subscription.setPlan(newPlan);
        subscription.setCurrentPrice(subscription.getBillingCycle() == Subscription.BillingCycle.YEARLY ? newPlan.getYearlyPrice() : newPlan.getMonthlyPrice());
        subscription.setUpdatedBy("SYSTEM");
        subscriptionRepository.save(subscription);
        log.info("Subscription plan changed successfully");
        auditService.log("Subscription", subscriptionId, "UPDATE", "SYSTEM", null, null);
    }

    public long getActiveSubscriptionsCount() {
        return subscriptionRepository.countByStatus(Subscription.SubscriptionStatus.ACTIVE);
    }

    private SubscriptionDTO mapToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .subscriptionNumber(subscription.getSubscriptionNumber())
                .userId(subscription.getUser().getId())
                .planId(subscription.getPlan().getId())
                .user(mapUserToDTO(subscription.getUser()))
                .plan(mapPlanToDTO(subscription.getPlan()))
                .status(subscription.getStatus())
                .billingCycle(subscription.getBillingCycle())
                .currentPrice(subscription.getCurrentPrice())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .nextBillingDate(subscription.getNextBillingDate())
                .autoRenew(subscription.getAutoRenew())
                .notes(subscription.getNotes())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private SubscriptionPlanDTO mapPlanToDTO(SubscriptionPlan plan) {
        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())
                .maxUsers(plan.getMaxUsers())
                .maxProjects(plan.getMaxProjects())
                .storageGB(plan.getStorageGB())
                .build();
    }

    private PagedResponse<SubscriptionDTO> mapPageToDTO(Page<Subscription> page) {
        List<SubscriptionDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<SubscriptionDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}