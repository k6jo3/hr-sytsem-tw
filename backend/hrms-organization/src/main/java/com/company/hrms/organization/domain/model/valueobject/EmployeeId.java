package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 員工 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class EmployeeId {

    private final UUID value;

    public EmployeeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("EmployeeId 不可為空");
        }
        this.value = value;
    }

    public EmployeeId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EmployeeId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

    public static EmployeeId of(UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId of(String value) {
        return new EmployeeId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
