package com.company.hrms.payroll.infrastructure.po;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 法扣款持久化物件
 */
@Entity
@Table(name = "hr04_legal_deductions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalDeductionPO {

    @Id
    @Column(name = "deduction_id", length = 36, nullable = false)
    private String deductionId;

    @Column(name = "employee_id", length = 36, nullable = false)
    private String employeeId;

    @Column(name = "court_order_number", length = 100, nullable = false)
    private String courtOrderNumber;

    @Column(name = "garnishment_type", length = 30, nullable = false)
    private String garnishmentType;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "deducted_amount", precision = 12, scale = 2)
    private BigDecimal deductedAmount;

    @Column(name = "remaining_amount", precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "issuing_authority", length = 100)
    private String issuingAuthority;

    @Column(name = "case_number", length = 100)
    private String caseNumber;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
