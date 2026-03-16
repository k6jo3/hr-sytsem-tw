package com.company.hrms.insurance.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投保級距JPA Entity
 */
@Entity
@Table(name = "insurance_levels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceLevelEntity {

    @Id
    @Column(name = "level_id")
    private UUID levelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false, length = 20)
    private InsuranceType insuranceType;

    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    @Column(name = "monthly_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlySalary;

    @Column(name = "labor_employee_rate", precision = 6, scale = 4)
    private BigDecimal laborEmployeeRate;

    @Column(name = "labor_employer_rate", precision = 6, scale = 4)
    private BigDecimal laborEmployerRate;

    @Column(name = "health_employee_rate", precision = 6, scale = 4)
    private BigDecimal healthEmployeeRate;

    @Column(name = "health_employer_rate", precision = 6, scale = 4)
    private BigDecimal healthEmployerRate;

    @Column(name = "pension_employer_rate", precision = 6, scale = 4)
    private BigDecimal pensionEmployerRate;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive;
}
