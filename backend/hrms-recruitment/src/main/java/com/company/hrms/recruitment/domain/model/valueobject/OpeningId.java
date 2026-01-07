package com.company.hrms.recruitment.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 職缺 ID
 */
public class OpeningId extends Identifier<UUID> {

    public OpeningId(UUID value) {
        super(value);
    }

    public static OpeningId create() {
        return new OpeningId(UUID.randomUUID());
    }

    public static OpeningId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OpeningId 不可為 null");
        }
        return new OpeningId(value);
    }

    public static OpeningId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OpeningId 不可為空");
        }
        return new OpeningId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OpeningId(value=" + getValue() + ")";
    }
}
