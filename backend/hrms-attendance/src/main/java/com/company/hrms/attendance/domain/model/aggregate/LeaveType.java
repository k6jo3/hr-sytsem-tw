package com.company.hrms.attendance.domain.model.aggregate;

import java.math.BigDecimal;

import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.attendance.domain.model.valueobject.StatutoryLeaveType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class LeaveType extends AggregateRoot<LeaveTypeId> {

    private String organizationId;
    private String name;
    private String code;
    private LeaveUnit unit;
    private boolean isPaid;
    private BigDecimal payRate;
    private boolean isActive;
    private boolean isStatutoryLeave;
    private StatutoryLeaveType statutoryType;
    private boolean requiresProof;
    private String proofDescription;
    private BigDecimal maxDaysPerYear;
    private boolean canCarryover;

    public LeaveType(LeaveTypeId id, String organizationId, String name, String code, LeaveUnit unit, boolean isPaid) {
        super(id);
        this.organizationId = organizationId;
        this.name = name;
        this.code = code;
        this.unit = unit;
        this.isPaid = isPaid;
        this.isActive = true;
        this.payRate = isPaid ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    public void updateDetails(String name, LeaveUnit unit, boolean isPaid, BigDecimal payRate,
            boolean requiresProof, String proofDescription, BigDecimal maxDaysPerYear, boolean canCarryover) {
        this.name = name;
        this.unit = unit;
        this.isPaid = isPaid;
        this.payRate = payRate;
        this.requiresProof = requiresProof;
        this.proofDescription = proofDescription;
        this.maxDaysPerYear = maxDaysPerYear;
        this.canCarryover = canCarryover;
    }

    public void setStatutory(boolean isStatutoryLeave, StatutoryLeaveType statutoryType) {
        this.isStatutoryLeave = isStatutoryLeave;
        this.statutoryType = statutoryType;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public static LeaveType reconstitute(LeaveTypeId id, String organizationId, String name, String code,
            LeaveUnit unit, boolean isPaid, BigDecimal payRate, boolean isActive,
            boolean isStatutoryLeave, StatutoryLeaveType statutoryType, boolean requiresProof,
            String proofDescription, BigDecimal maxDaysPerYear, boolean canCarryover) {
        LeaveType type = new LeaveType(id, organizationId, name, code, unit, isPaid);
        type.payRate = payRate;
        type.isActive = isActive;
        type.isStatutoryLeave = isStatutoryLeave;
        type.statutoryType = statutoryType;
        type.requiresProof = requiresProof;
        type.proofDescription = proofDescription;
        type.maxDaysPerYear = maxDaysPerYear;
        type.canCarryover = canCarryover;
        return type;
    }
}
