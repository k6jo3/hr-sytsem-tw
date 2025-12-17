package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 學歷 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class EducationId {

    private final UUID value;

    public EducationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("EducationId 不可為空");
        }
        this.value = value;
    }

    public EducationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EducationId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static EducationId generate() {
        return new EducationId(UUID.randomUUID());
    }

    public static EducationId of(UUID value) {
        return new EducationId(value);
    }

    public static EducationId of(String value) {
        return new EducationId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
