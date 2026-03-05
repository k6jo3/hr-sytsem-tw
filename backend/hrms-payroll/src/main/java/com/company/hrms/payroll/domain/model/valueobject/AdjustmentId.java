package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資調整單 ID 值物件
 */
@Getter
@EqualsAndHashCode
public class AdjustmentId {

    private final String value;

    public AdjustmentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AdjustmentId cannot be null or blank");
        }
        this.value = value;
    }

    public static AdjustmentId generate() {
        return new AdjustmentId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
