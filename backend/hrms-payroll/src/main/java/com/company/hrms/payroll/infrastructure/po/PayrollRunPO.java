package com.company.hrms.payroll.infrastructure.po;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

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
 * 薪資批次 Persistent Object
 */
@Entity
@Table(name = "hr04_payroll_runs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunPO {

    @Id
    @Column(name = "run_id", length = 36)
    @QueryFilter(operator = Operator.EQ)
    private String runId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "organization_id", length = 36, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    @Column(name = "payroll_system", length = 20, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String payrollSystem;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "status", length = 20, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String status;

    @Column(name = "executor_id", length = 36)
    private String executorId;

    @Column(name = "approver_id", length = 36)
    private String approverId;

    @Column(name = "total_employees")
    private Integer totalEmployees;

    @Column(name = "processed_employees")
    private Integer processedEmployees;

    @Column(name = "failed_employees")
    private Integer failedEmployees;

    @Column(name = "total_gross_amount", precision = 12, scale = 2)
    private BigDecimal totalGrossAmount;

    @Column(name = "total_net_amount", precision = 12, scale = 2)
    private BigDecimal totalNetAmount;

    @Column(name = "total_deductions", precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "total_overtime_pay", precision = 12, scale = 2)
    private BigDecimal totalOvertimePay;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "submitted_by", length = 36)
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "bank_file_url", length = 255)
    private String bankFileUrl;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_by", length = 36)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
