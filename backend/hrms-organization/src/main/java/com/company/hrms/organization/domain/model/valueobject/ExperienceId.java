package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 工作經歷 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class ExperienceId {

    private final UUID value;

    public ExperienceId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ExperienceId 不可為空");
        }
        this.value = value;
    }

    public ExperienceId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExperienceId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static ExperienceId generate() {
        return new ExperienceId(UUID.randomUUID());
    }

    public static ExperienceId of(UUID value) {
        return new ExperienceId(value);
    }

    public static ExperienceId of(String value) {
        return new ExperienceId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
