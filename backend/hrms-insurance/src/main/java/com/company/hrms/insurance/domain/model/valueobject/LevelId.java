package com.company.hrms.insurance.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 投保級距ID
 */
public class LevelId {
    private final String value;

    public LevelId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LevelId cannot be null or blank");
        }
        this.value = value;
    }

    public static LevelId generate() {
        return new LevelId(UUID.randomUUID().toString());
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
        LevelId that = (LevelId) o;
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
