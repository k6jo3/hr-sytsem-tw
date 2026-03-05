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
 * 薪資結構 Persistent Object
 */
@Entity
@Table(name = "hr04_salary_structures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructurePO {

    @Id
    @Column(name = "structure_id", length = 36)
    @QueryFilter(operator = Operator.EQ)
    private String structureId;

    @Column(name = "employee_id", length = 36, nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private String employeeId;

    @Column(name = "monthly_salary", precision = 10, scale = 2)
    private BigDecimal monthlySalary;

    @Column(name = "daily_rate", precision = 12, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "payroll_system", length = 20, nullable = false)
    private String payrollSystem; // MONTHLY, DAILY, HOURLY

    @Column(name = "payroll_cycle", length = 20, nullable = false)
    private String payrollCycle; // MONTHLY, BI_WEEKLY...

    @Column(name = "payment_method", length = 20, nullable = false)
    @Builder.Default
    private String paymentMethod = "BANK_TRANSFER"; // BANK_TRANSFER, CASH

    @Column(name = "effective_date", nullable = false)
    @QueryFilter(operator = Operator.GTE)
    private LocalDate effectiveDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    @QueryFilter(operator = Operator.EQ)
    private boolean active;

    @OneToMany(mappedBy = "salaryStructure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SalaryItemPO> items = new ArrayList<>();

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
