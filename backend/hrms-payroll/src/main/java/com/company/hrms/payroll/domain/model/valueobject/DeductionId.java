package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.ValueObject;

/**
 * 法扣款 ID 值物件
 */
public class DeductionId extends ValueObject {
    private final String value;

    public DeductionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DeductionId cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DeductionId generate() {
        return new DeductionId(UUID.randomUUID().toString());
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
