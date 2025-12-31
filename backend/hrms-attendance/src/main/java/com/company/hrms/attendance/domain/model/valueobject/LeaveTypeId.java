package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 假別 ID
 */
public class LeaveTypeId extends Identifier<String> {
    public LeaveTypeId(String value) {
        super(value);
    }

    public static LeaveTypeId next() {
        return new LeaveTypeId(generateUUID());
    }
}
