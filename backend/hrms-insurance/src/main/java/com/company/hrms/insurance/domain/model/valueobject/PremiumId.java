package com.company.hrms.insurance.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 補充保費ID
 */
public class PremiumId {
    private final String value;

    public PremiumId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PremiumId cannot be null or blank");
        }
        this.value = value;
    }

    public static PremiumId generate() {
        return new PremiumId(UUID.randomUUID().toString());
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
        PremiumId that = (PremiumId) o;
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
