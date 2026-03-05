package com.company.hrms.insurance.domain.model.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * 團體保險方案職等對應實體
 *
 * <p>定義特定職等在團保方案中的保障金額、保費及公司/員工費用拆分。
 */
@Getter
@Builder
public class PlanTier {

    private final String tierId;
    private String jobGrade;
    private BigDecimal coverageAmount;
    private BigDecimal monthlyPremium;
    private BigDecimal employerShareRate;

    /**
     * 建立職等方案
     *
     * @param jobGrade         職等（如 M1, M2, E1, E2）
     * @param coverageAmount   保障金額
     * @param monthlyPremium   月繳保費總額
     * @param employerShareRate 公司負擔比例（0.0 ~ 1.0）
     */
    public static PlanTier create(String jobGrade, BigDecimal coverageAmount,
            BigDecimal monthlyPremium, BigDecimal employerShareRate) {
        if (employerShareRate.compareTo(BigDecimal.ZERO) < 0
                || employerShareRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("公司負擔比例必須介於 0 到 1 之間");
        }
        return PlanTier.builder()
                .tierId(UUID.randomUUID().toString())
                .jobGrade(jobGrade)
                .coverageAmount(coverageAmount)
                .monthlyPremium(monthlyPremium)
                .employerShareRate(employerShareRate)
                .build();
    }

    /**
     * 公司負擔金額
     */
    public BigDecimal getEmployerAmount() {
        return monthlyPremium.multiply(employerShareRate).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 員工自付金額
     */
    public BigDecimal getEmployeeAmount() {
        return monthlyPremium.subtract(getEmployerAmount());
    }
}
