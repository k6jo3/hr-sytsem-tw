package com.company.hrms.recruitment.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

/**
 * Offer ID
 */
public class OfferId extends Identifier<UUID> {

    public OfferId(UUID value) {
        super(value);
    }

    public static OfferId create() {
        return new OfferId(UUID.randomUUID());
    }

    public static OfferId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OfferId 不可為 null");
        }
        return new OfferId(value);
    }

    public static OfferId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OfferId 不可為空");
        }
        return new OfferId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OfferId(value=" + getValue() + ")";
    }
}
