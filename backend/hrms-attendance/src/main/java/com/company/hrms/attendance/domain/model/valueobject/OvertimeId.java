package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 加班單 ID
 */
public class OvertimeId extends Identifier<String> {
    public OvertimeId(String value) {
        super(value);
    }

    public static OvertimeId next() {
        return new OvertimeId(generateUUID());
    }
}
