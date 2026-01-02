package com.company.hrms.performance.domain.model.valueobject;

import java.util.UUID;

import lombok.Value;

/**
 * 考核週期 ID
 */
@Value
public class CycleId {
    UUID value;

    public static CycleId create() {
        return new CycleId(UUID.randomUUID());
    }

    public static CycleId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CycleId 不可為 null");
        }
        return new CycleId(value);
    }

    public static CycleId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CycleId 不可為空");
        }
        return new CycleId(UUID.fromString(value));
    }
}
