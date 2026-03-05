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
 * 薪資預借持久化物件
 */
@Entity
@Table(name = "hr04_salary_advances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryAdvancePO {

    @Id
    @Column(name = "advance_id", length = 36, nullable = false)
    private String advanceId;

    @Column(name = "employee_id", length = 36, nullable = false)
    private String employeeId;

    @Column(name = "requested_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "approved_amount", precision = 10, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "installment_months", nullable = false)
    private Integer installmentMonths;

    @Column(name = "installment_amount", precision = 10, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "repaid_amount", precision = 10, scale = 2)
    private BigDecimal repaidAmount;

    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "approver_id", length = 36)
    private String approverId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
