package com.company.hrms.performance.domain.model.valueobject;

import java.util.UUID;

/**
 * 考核記錄 ID
 */
public class ReviewId {
    private final UUID value;

    public ReviewId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static ReviewId create() {
        return new ReviewId(UUID.randomUUID());
    }

    public static ReviewId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ReviewId 不可為 null");
        }
        return new ReviewId(value);
    }

    public static ReviewId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ReviewId 不可為空");
        }
        return new ReviewId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReviewId reviewId = (ReviewId) o;
        return value.equals(reviewId.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ReviewId(value=" + value + ")";
    }
}
