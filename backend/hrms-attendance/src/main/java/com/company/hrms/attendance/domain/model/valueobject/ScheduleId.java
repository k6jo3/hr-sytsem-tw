package com.company.hrms.attendance.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.ValueObject;

/**
 * 排班表 ID 值物件
 */
public class ScheduleId extends ValueObject {
    private final String value;

    public ScheduleId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ScheduleId cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ScheduleId generate() {
        return new ScheduleId(UUID.randomUUID().toString());
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
