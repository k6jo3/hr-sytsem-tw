package com.company.hrms.insurance.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.company.hrms.insurance.domain.model.entity.PlanTier;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

import lombok.Builder;
import lombok.Getter;

/**
 * 團體保險方案聚合根
 *
 * <p>管理團體保險方案的基本資訊、費率與職等對應。
 * 每個方案對應一種團體保險類型（壽險/傷害險/醫療險），
 * 透過 PlanTier 定義不同職等的保障內容與費用拆分。
 */
@Getter
@Builder
public class GroupInsurancePlan {

    private final String planId;
    private final String organizationId;
    private String planName;
    private String planCode;
    private InsuranceType insuranceType;
    private String insurerName;
    private String policyNumber;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private boolean active;

    @Builder.Default
    private List<PlanTier> tiers = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立新方案
     */
    public static GroupInsurancePlan create(
            String organizationId,
            String planName,
            String planCode,
            InsuranceType insuranceType,
            String insurerName,
            String policyNumber,
            LocalDate contractStartDate,
            LocalDate contractEndDate) {

        if (!insuranceType.isGroupInsurance()) {
            throw new IllegalArgumentException("保險類型必須為團體保險: " + insuranceType);
        }

        return GroupInsurancePlan.builder()
                .planId(UUID.randomUUID().toString())
                .organizationId(organizationId)
                .planName(planName)
                .planCode(planCode)
                .insuranceType(insuranceType)
                .insurerName(insurerName)
                .policyNumber(policyNumber)
                .contractStartDate(contractStartDate)
                .contractEndDate(contractEndDate)
                .active(true)
                .tiers(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 新增職等方案對應
     *
     * @param jobGrade         職等
     * @param coverageAmount   保障金額
     * @param monthlyPremium   月繳保費
     * @param employerShareRate 公司負擔比例 (0~1)
     */
    public PlanTier addTier(String jobGrade, BigDecimal coverageAmount,
            BigDecimal monthlyPremium, BigDecimal employerShareRate) {

        boolean exists = tiers.stream().anyMatch(t -> t.getJobGrade().equals(jobGrade));
        if (exists) {
            throw new IllegalStateException("該職等已存在方案對應: " + jobGrade);
        }

        PlanTier tier = PlanTier.create(jobGrade, coverageAmount, monthlyPremium, employerShareRate);
        tiers.add(tier);
        this.updatedAt = LocalDateTime.now();
        return tier;
    }

    /**
     * 依職等查詢方案
     */
    public Optional<PlanTier> findTierByJobGrade(String jobGrade) {
        return tiers.stream()
                .filter(t -> t.getJobGrade().equals(jobGrade))
                .findFirst();
    }

    /**
     * 停用方案
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 合約是否在有效期間內
     */
    public boolean isContractValidOn(LocalDate date) {
        return !date.isBefore(contractStartDate)
                && (contractEndDate == null || !date.isAfter(contractEndDate));
    }
}
