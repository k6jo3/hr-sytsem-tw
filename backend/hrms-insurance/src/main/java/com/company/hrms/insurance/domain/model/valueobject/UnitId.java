package com.company.hrms.insurance.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 投保單位ID
 */
public class UnitId {
    private final String value;

    public UnitId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UnitId cannot be null or blank");
        }
        this.value = value;
    }

    public static UnitId generate() {
        return new UnitId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UnitId that = (UnitId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
