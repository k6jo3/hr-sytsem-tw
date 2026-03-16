package com.company.hrms.payroll.infrastructure.po;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 薪資單 Persistent Object
 */
@Entity
@Table(name = "hr04_payslips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipPO {

    @Id
    @Column(name = "payslip_id", length = 36)
    @QueryFilter(operator = Operator.EQ)
    private String payslipId;

    @Column(name = "run_id", length = 36, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String runId;

    @Column(name = "employee_id", length = 36, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String employeeId;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "employee_name", length = 100)
    @QueryFilter(operator = Operator.LIKE)
    private String employeeName;

    @Column(name = "period_start_date")
    private LocalDate periodStartDate;

    @Column(name = "period_end_date")
    private LocalDate periodEndDate;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "gross_wage", precision = 10, scale = 2)
    private BigDecimal grossWage;

    @Column(name = "net_wage", precision = 10, scale = 2)
    private BigDecimal netWage;

    @Column(name = "income_tax", precision = 10, scale = 2)
    private BigDecimal incomeTax;

    @Column(name = "leave_deduction", precision = 10, scale = 2)
    private BigDecimal leaveDeduction;

    // Overtime Details Breakdown
    @Column(name = "overtime_pay_total", precision = 10, scale = 2)
    private BigDecimal overtimePayTotal;

    @Column(name = "ot_weekday_hours", precision = 5, scale = 2)
    private BigDecimal otWeekdayHours;

    @Column(name = "ot_weekday_pay", precision = 10, scale = 2)
    private BigDecimal otWeekdayPay;

    @Column(name = "ot_restday_hours", precision = 5, scale = 2)
    private BigDecimal otRestDayHours;

    @Column(name = "ot_restday_pay", precision = 10, scale = 2)
    private BigDecimal otRestDayPay;

    @Column(name = "ot_holiday_hours", precision = 5, scale = 2)
    private BigDecimal otHolidayHours;

    @Column(name = "ot_holiday_pay", precision = 10, scale = 2)
    private BigDecimal otHolidayPay;

    // Insurance
    @Column(name = "ins_labor_fee", precision = 10, scale = 2)
    private BigDecimal insLaborFee;

    @Column(name = "ins_health_fee", precision = 10, scale = 2)
    private BigDecimal insHealthFee;

    @Column(name = "ins_pension_fee", precision = 10, scale = 2)
    private BigDecimal insPensionFee; // 自提

    @Column(name = "ins_supplementary_fee", precision = 10, scale = 2)
    private BigDecimal insSupplementaryFee; // 補充保費

    // Bank Account
    @Column(name = "bank_code", length = 10)
    private String bankCode;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "status", length = 20, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String status;

    @Column(name = "pdf_url", length = 255)
    private String pdfUrl;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private List<PayslipItemPO> items = new ArrayList<>();

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
