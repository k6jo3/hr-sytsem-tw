package com.company.hrms.insurance.domain.model.valueobject;

import java.util.UUID;

/**
 * 眷屬ID Value Object
 */
public class DependentId {
    private final String value;

    public DependentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DependentId cannot be null or blank");
        }
        this.value = value;
    }

    public static DependentId generate() {
        return new DependentId(UUID.randomUUID().toString());
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
        DependentId that = (DependentId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
