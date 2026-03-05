package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 排班表 ID 值物件
 */
public class ScheduleId extends Identifier<String> {

    public ScheduleId(String value) {
        super(value);
    }

    public static ScheduleId generate() {
        return new ScheduleId(generateUUID());
    }
}
