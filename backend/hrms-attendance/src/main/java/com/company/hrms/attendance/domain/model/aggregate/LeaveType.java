package com.company.hrms.attendance.domain.model.aggregate;

import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class LeaveType extends AggregateRoot<LeaveTypeId> {

    private String name;
    private String code; // e.g., ANNUAL, SICK, usage of StatutoryLeaveType name
    private LeaveUnit unit;
    private boolean isPaid;

    public LeaveType(LeaveTypeId id, String name, String code, LeaveUnit unit, boolean isPaid) {
        super(id);
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Code cannot be empty");
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");

        this.name = name;
        this.code = code;
        this.unit = unit;
        this.isPaid = isPaid;
    }
}
