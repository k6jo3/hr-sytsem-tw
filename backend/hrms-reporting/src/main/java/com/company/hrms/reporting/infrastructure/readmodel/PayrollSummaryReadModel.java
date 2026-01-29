package com.company.hrms.reporting.infrastructure.readmodel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 薪資匯總讀模型
 * 
 * <p>
 * 從薪資服務的事件更新
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Entity
@Table(name = "rm_payroll_summary", indexes = {
        @Index(name = "idx_tenant_month", columnList = "tenant_id,year_month"),
        @Index(name = "idx_employee_month", columnList = "employee_id,year_month"),
        @Index(name = "idx_department_month", columnList = "department_id,year_month")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSummaryReadModel {

    @Id
    @Column(name = "id", length = 100)
    private String id; // employeeId + yearMonth

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "employee_name", length = 100)
    private String employeeName;

    @Column(name = "department_id", length = 50)
    private String departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "year_month", length = 7)
    private String yearMonth; // YYYY-MM

    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "overtime_pay", precision = 15, scale = 2)
    private BigDecimal overtimePay;

    @Column(name = "allowances", precision = 15, scale = 2)
    private BigDecimal allowances;

    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @Column(name = "gross_pay", precision = 15, scale = 2)
    private BigDecimal grossPay;

    @Column(name = "labor_insurance", precision = 15, scale = 2)
    private BigDecimal laborInsurance;

    @Column(name = "health_insurance", precision = 15, scale = 2)
    private BigDecimal healthInsurance;

    @Column(name = "income_tax", precision = 15, scale = 2)
    private BigDecimal incomeTax;

    @Column(name = "other_deductions", precision = 15, scale = 2)
    private BigDecimal otherDeductions;

    @Column(name = "net_pay", precision = 15, scale = 2)
    private BigDecimal netPay;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
