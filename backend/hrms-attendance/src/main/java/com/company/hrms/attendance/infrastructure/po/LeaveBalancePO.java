package com.company.hrms.attendance.infrastructure.po;

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

@Entity
@Table(name = "leave_balances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalancePO {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "leave_type_id", length = 50, nullable = false)
    private String leaveTypeId;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Column(name = "total_days", precision = 5, scale = 2)
    private BigDecimal totalDays;

    @Column(name = "used_days", precision = 5, scale = 2)
    private BigDecimal usedDays;

    @Column(name = "carry_over_days", precision = 5, scale = 2)
    private BigDecimal carryOverDays;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
