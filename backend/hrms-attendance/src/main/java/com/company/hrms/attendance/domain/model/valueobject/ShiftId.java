package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 班別 ID
 */
public class ShiftId extends Identifier<String> {
    public ShiftId(String value) {
        super(value);
    }

    public static ShiftId next() {
        return new ShiftId(generateUUID());
    }
}
