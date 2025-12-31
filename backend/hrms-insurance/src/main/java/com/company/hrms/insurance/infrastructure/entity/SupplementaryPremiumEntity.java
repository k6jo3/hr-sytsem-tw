package com.company.hrms.insurance.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.insurance.domain.model.valueobject.IncomeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * 補充保費JPA Entity
 */
@Entity
@Table(name = "supplementary_premiums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementaryPremiumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "premium_id")
    private UUID premiumId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "income_type", nullable = false, length = 30)
    private IncomeType incomeType;

    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @Column(name = "income_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal incomeAmount;

    @Column(name = "insured_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal insuredSalary;

    @Column(name = "threshold", nullable = false, precision = 12, scale = 2)
    private BigDecimal threshold;

    @Column(name = "premium_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumBase;

    @Column(name = "premium_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumAmount;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
