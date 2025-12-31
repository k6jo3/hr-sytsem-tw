package com.company.hrms.insurance.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 加退保記錄ID
 */
public class EnrollmentId {
    private final String value;

    public EnrollmentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EnrollmentId cannot be null or blank");
        }
        this.value = value;
    }

    public static EnrollmentId generate() {
        return new EnrollmentId(UUID.randomUUID().toString());
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
        EnrollmentId that = (EnrollmentId) o;
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
