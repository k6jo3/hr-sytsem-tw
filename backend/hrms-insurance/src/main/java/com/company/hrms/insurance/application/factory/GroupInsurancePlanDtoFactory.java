package com.company.hrms.insurance.application.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;
import com.company.hrms.insurance.api.response.GroupInsurancePlanResponse;
import com.company.hrms.insurance.api.response.PlanTierResponse;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.entity.PlanTier;

/**
 * 團體保險方案 DTO 轉換工廠
 * 負責 Domain Model -> Response DTO 的轉換
 */
@Component
public class GroupInsurancePlanDtoFactory {

    /**
     * 轉換為列表回應 DTO
     */
    public GroupInsurancePlanResponse toResponse(GroupInsurancePlan plan) {
        return GroupInsurancePlanResponse.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .planCode(plan.getPlanCode())
                .insuranceType(plan.getInsuranceType().name())
                .insuranceTypeDisplay(plan.getInsuranceType().getDisplayName())
                .insurerName(plan.getInsurerName())
                .policyNumber(plan.getPolicyNumber())
                .active(plan.isActive())
                .contractStartDate(plan.getContractStartDate())
                .contractEndDate(plan.getContractEndDate())
                .tierCount(plan.getTiers() != null ? plan.getTiers().size() : 0)
                .build();
    }

    /**
     * 轉換為詳情回應 DTO（含職等方案列表）
     */
    public GroupInsurancePlanDetailResponse toDetailResponse(GroupInsurancePlan plan) {
        List<PlanTierResponse> tierResponses = plan.getTiers() != null
                ? plan.getTiers().stream()
                        .map(this::toTierResponse)
                        .collect(Collectors.toList())
                : List.of();

        return GroupInsurancePlanDetailResponse.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .planCode(plan.getPlanCode())
                .insuranceType(plan.getInsuranceType().name())
                .insuranceTypeDisplay(plan.getInsuranceType().getDisplayName())
                .insurerName(plan.getInsurerName())
                .policyNumber(plan.getPolicyNumber())
                .active(plan.isActive())
                .contractStartDate(plan.getContractStartDate())
                .contractEndDate(plan.getContractEndDate())
                .tierCount(tierResponses.size())
                .tiers(tierResponses)
                .build();
    }

    /**
     * 轉換職等方案為回應 DTO
     */
    public PlanTierResponse toTierResponse(PlanTier tier) {
        return PlanTierResponse.builder()
                .tierId(tier.getTierId())
                .jobGrade(tier.getJobGrade())
                .coverageAmount(tier.getCoverageAmount())
                .monthlyPremium(tier.getMonthlyPremium())
                .employerShareRate(tier.getEmployerShareRate())
                .employerAmount(tier.getEmployerAmount())
                .employeeAmount(tier.getEmployeeAmount())
                .build();
    }
}
