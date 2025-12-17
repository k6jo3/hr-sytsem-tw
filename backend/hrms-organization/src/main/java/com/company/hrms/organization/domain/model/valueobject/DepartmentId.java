package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 部門 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class DepartmentId {

    private final UUID value;

    public DepartmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("DepartmentId 不可為空");
        }
        this.value = value;
    }

    public DepartmentId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DepartmentId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID());
    }

    public static DepartmentId of(UUID value) {
        return new DepartmentId(value);
    }

    public static DepartmentId of(String value) {
        return new DepartmentId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
