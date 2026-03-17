package com.company.hrms.insurance.infrastructure.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 團體保險方案 JPA Entity
 */
@Entity
@Table(name = "group_insurance_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInsurancePlanEntity {

    @Id
    @Column(name = "plan_id", length = 36)
    private String planId;

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Column(name = "plan_code", nullable = false, unique = true, length = 50)
    private String planCode;

    @Column(name = "insurance_type", nullable = false, length = 30)
    private String insuranceType;

    @Column(name = "insurer_name", length = 100)
    private String insurerName;

    @Column(name = "policy_number", length = 50)
    private String policyNumber;

    @Column(name = "contract_start_date", nullable = false)
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
