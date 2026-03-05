package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.ValueObject;

/**
 * 薪資預借 ID 值物件
 */
public class AdvanceId extends ValueObject {
    private final String value;

    public AdvanceId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AdvanceId cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AdvanceId generate() {
        return new AdvanceId(UUID.randomUUID().toString());
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
