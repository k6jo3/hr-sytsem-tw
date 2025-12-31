package com.company.hrms.insurance.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投保單位JPA Entity
 */
@Entity
@Table(name = "insurance_units")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "unit_id")
    private UUID unitId;

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "unit_code", nullable = false, length = 50, unique = true)
    private String unitCode;

    @Column(name = "unit_name", nullable = false, length = 255)
    private String unitName;

    @Column(name = "labor_insurance_number", length = 50)
    private String laborInsuranceNumber;

    @Column(name = "health_insurance_number", length = 50)
    private String healthInsuranceNumber;

    @Column(name = "pension_number", length = 50)
    private String pensionNumber;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}
