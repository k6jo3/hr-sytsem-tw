package com.company.hrms.attendance.domain.model.aggregate;

import java.math.BigDecimal;

import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class LeaveBalance extends AggregateRoot<BalanceId> {

    private String employeeId;
    private LeaveTypeId leaveTypeId;
    private int year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;

    public LeaveBalance(BalanceId id, String employeeId, LeaveTypeId leaveTypeId,
            int year, BigDecimal totalDays) {
        super(id);
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = BigDecimal.ZERO;
    }

    public void deduct(BigDecimal days) {
        if (days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduction days must be positive");
        }
        if (getRemainingDays().compareTo(days) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.usedDays = this.usedDays.add(days);
    }

    public void restore(BigDecimal days) {
        if (days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Restore days must be positive");
        }
        if (this.usedDays.compareTo(days) < 0) {
            throw new IllegalArgumentException("Cannot restore more than used");
        }
        this.usedDays = this.usedDays.subtract(days);
    }

    public BigDecimal getRemainingDays() {
        return totalDays.subtract(usedDays);
    }
}
