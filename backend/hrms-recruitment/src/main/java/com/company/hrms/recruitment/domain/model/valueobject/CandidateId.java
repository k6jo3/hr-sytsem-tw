package com.company.hrms.recruitment.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 應徵者 ID
 */
public class CandidateId extends Identifier<UUID> {

    public CandidateId(UUID value) {
        super(value);
    }

    public static CandidateId create() {
        return new CandidateId(UUID.randomUUID());
    }

    public static CandidateId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CandidateId 不可為 null");
        }
        return new CandidateId(value);
    }

    public static CandidateId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CandidateId 不可為空");
        }
        return new CandidateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CandidateId(value=" + getValue() + ")";
    }
}
