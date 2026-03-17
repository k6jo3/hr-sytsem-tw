package com.company.hrms.insurance.infrastructure.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 團體保險方案職等對應 JPA Entity
 */
@Entity
@Table(name = "group_insurance_plan_tiers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTierEntity {

    @Id
    @Column(name = "tier_id", length = 36)
    private String tierId;

    @Column(name = "plan_id", nullable = false, length = 36)
    private String planId;

    @Column(name = "job_grade", nullable = false, length = 20)
    private String jobGrade;

    @Column(name = "coverage_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal coverageAmount;

    @Column(name = "monthly_premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPremium;

    @Column(name = "employer_share_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal employerShareRate;
}
