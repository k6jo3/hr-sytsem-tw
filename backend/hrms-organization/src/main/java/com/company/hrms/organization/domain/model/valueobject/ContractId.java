package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 合約 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class ContractId {

    private final UUID value;

    public ContractId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ContractId 不可為空");
        }
        this.value = value;
    }

    public ContractId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ContractId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static ContractId generate() {
        return new ContractId(UUID.randomUUID());
    }

    public static ContractId of(UUID value) {
        return new ContractId(value);
    }

    public static ContractId of(String value) {
        return new ContractId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
