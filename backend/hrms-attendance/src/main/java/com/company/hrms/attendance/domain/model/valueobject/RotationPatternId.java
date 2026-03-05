package com.company.hrms.attendance.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.ValueObject;

/**
 * 輪班模式 ID 值物件
 */
public class RotationPatternId extends ValueObject {
    private final String value;

    public RotationPatternId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RotationPatternId cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RotationPatternId generate() {
        return new RotationPatternId(UUID.randomUUID().toString());
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
