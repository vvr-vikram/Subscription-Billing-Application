package com.billing.subscription.service;

import com.billing.subscription.dto.SubscriptionPlanDTO;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.SubscriptionPlan;
import com.billing.subscription.exception.DuplicateResourceException;
import com.billing.subscription.exception.ResourceNotFoundException;
import com.billing.subscription.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final AuditService auditService;

    public SubscriptionPlanDTO createPlan(SubscriptionPlanDTO planDTO) {
        log.info("Creating new subscription plan: {}", planDTO.getName());
        
        if (planRepository.existsByName(planDTO.getName())) {
            throw new DuplicateResourceException("Plan with name already exists: " + planDTO.getName());
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(planDTO.getName())
                .description(planDTO.getDescription())
                .monthlyPrice(planDTO.getMonthlyPrice())
                .yearlyPrice(planDTO.getYearlyPrice())
                .maxUsers(planDTO.getMaxUsers())
                .maxProjects(planDTO.getMaxProjects())
                .storageGB(planDTO.getStorageGB())
                .status(SubscriptionPlan.PlanStatus.ACTIVE)
                .featured(planDTO.getFeatured() != null ? planDTO.getFeatured() : false)
                .features(planDTO.getFeatures())
                .createdBy("SYSTEM")
                .build();

        SubscriptionPlan savedPlan = planRepository.save(plan);
        log.info("Plan created successfully with ID: {}", savedPlan.getId());
        auditService.log("SubscriptionPlan", savedPlan.getId(), "CREATE", "SYSTEM", null, null);
        
        return mapToDTO(savedPlan);
    }

    public SubscriptionPlanDTO getPlanById(Long id) {
        log.info("Fetching plan with ID: {}", id);
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));
        return mapToDTO(plan);
    }

    public PagedResponse<SubscriptionPlanDTO> getAllPlans(Pageable pageable) {
        log.info("Fetching all plans");
        Page<SubscriptionPlan> plans = planRepository.findAll(pageable);
        return mapPageToDTO(plans);
    }

    public PagedResponse<SubscriptionPlanDTO> searchPlans(String search, Pageable pageable) {
        log.info("Searching plans with keyword: {}", search);
        Page<SubscriptionPlan> plans = planRepository.searchPlans(search, pageable);
        return mapPageToDTO(plans);
    }

    public PagedResponse<SubscriptionPlanDTO> getActivePlans(Pageable pageable) {
        log.info("Fetching active plans");
        Page<SubscriptionPlan> plans = planRepository.findByStatus(SubscriptionPlan.PlanStatus.ACTIVE, pageable);
        return mapPageToDTO(plans);
    }

    public PagedResponse<SubscriptionPlanDTO> getFeaturedPlans(Pageable pageable) {
        log.info("Fetching featured plans");
        Page<SubscriptionPlan> plans = planRepository.findByFeatured(true, pageable);
        return mapPageToDTO(plans);
    }

    public SubscriptionPlanDTO updatePlan(Long id, SubscriptionPlanDTO planDTO) {
        log.info("Updating plan with ID: {}", id);
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));

        String oldValues = plan.toString();

        if (planDTO.getName() != null && !planDTO.getName().equals(plan.getName())) {
            if (planRepository.existsByName(planDTO.getName())) {
                throw new DuplicateResourceException("Plan with name already exists: " + planDTO.getName());
            }
            plan.setName(planDTO.getName());
        }

        if (planDTO.getDescription() != null) {
            plan.setDescription(planDTO.getDescription());
        }
        if (planDTO.getMonthlyPrice() != null) {
            plan.setMonthlyPrice(planDTO.getMonthlyPrice());
        }
        if (planDTO.getYearlyPrice() != null) {
            plan.setYearlyPrice(planDTO.getYearlyPrice());
        }
        if (planDTO.getMaxUsers() != null) {
            plan.setMaxUsers(planDTO.getMaxUsers());
        }
        if (planDTO.getMaxProjects() != null) {
            plan.setMaxProjects(planDTO.getMaxProjects());
        }
        if (planDTO.getStorageGB() != null) {
            plan.setStorageGB(planDTO.getStorageGB());
        }
        if (planDTO.getFeatured() != null) {
            plan.setFeatured(planDTO.getFeatured());
        }
        if (planDTO.getFeatures() != null) {
            plan.setFeatures(planDTO.getFeatures());
        }

        plan.setUpdatedBy("SYSTEM");
        SubscriptionPlan updatedPlan = planRepository.save(plan);
        log.info("Plan updated successfully with ID: {}", updatedPlan.getId());
        auditService.log("SubscriptionPlan", updatedPlan.getId(), "UPDATE", "SYSTEM", oldValues, plan.toString());
        
        return mapToDTO(updatedPlan);
    }

    public void deletePlan(Long id) {
        log.info("Deleting plan with ID: {}", id);
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));
        
        planRepository.delete(plan);
        log.info("Plan deleted successfully with ID: {}", id);
        auditService.log("SubscriptionPlan", id, "DELETE", "SYSTEM", plan.toString(), null);
    }

    public void changePlanStatus(Long id, SubscriptionPlan.PlanStatus status) {
        log.info("Changing plan status to: {} for plan ID: {}", status, id);
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));
        
        plan.setStatus(status);
        plan.setUpdatedBy("SYSTEM");
        planRepository.save(plan);
        log.info("Plan status changed successfully");
    }

    private SubscriptionPlanDTO mapToDTO(SubscriptionPlan plan) {
        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())
                .maxUsers(plan.getMaxUsers())
                .maxProjects(plan.getMaxProjects())
                .storageGB(plan.getStorageGB())
                .status(plan.getStatus())
                .featured(plan.getFeatured())
                .features(plan.getFeatures())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private PagedResponse<SubscriptionPlanDTO> mapPageToDTO(Page<SubscriptionPlan> page) {
        List<SubscriptionPlanDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return PagedResponse.<SubscriptionPlanDTO>builder()
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