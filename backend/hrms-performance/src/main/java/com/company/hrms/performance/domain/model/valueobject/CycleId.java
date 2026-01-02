package com.company.hrms.performance.domain.model.valueobject;

import java.util.UUID;

/**
 * 考核週期 ID
 */
public class CycleId {
    private final UUID value;

    public CycleId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CycleId cycleId = (CycleId) o;
        return value.equals(cycleId.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CycleId(value=" + value + ")";
    }
}
