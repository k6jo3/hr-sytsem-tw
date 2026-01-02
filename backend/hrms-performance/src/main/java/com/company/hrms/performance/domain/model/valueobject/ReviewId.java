package com.company.hrms.performance.domain.model.valueobject;

import java.util.UUID;

import lombok.Value;

/**
 * 考核記錄 ID
 */
@Value
public class ReviewId {
    UUID value;

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
}
