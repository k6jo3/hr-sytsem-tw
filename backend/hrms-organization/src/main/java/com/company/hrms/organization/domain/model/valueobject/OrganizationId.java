package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 組織 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class OrganizationId {

    private final UUID value;

    public OrganizationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OrganizationId 不可為空");
        }
        this.value = value;
    }

    public OrganizationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrganizationId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static OrganizationId generate() {
        return new OrganizationId(UUID.randomUUID());
    }

    public static OrganizationId of(UUID value) {
        return new OrganizationId(value);
    }

    public static OrganizationId of(String value) {
        return new OrganizationId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
