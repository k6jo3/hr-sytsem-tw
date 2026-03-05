package com.company.hrms.attendance.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.ValueObject;

/**
 * 換班申請 ID 值物件
 */
public class SwapRequestId extends ValueObject {
    private final String value;

    public SwapRequestId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SwapRequestId cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SwapRequestId generate() {
        return new SwapRequestId(UUID.randomUUID().toString());
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
