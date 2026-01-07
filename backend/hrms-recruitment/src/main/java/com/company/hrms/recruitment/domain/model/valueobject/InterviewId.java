package com.company.hrms.recruitment.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 面試 ID
 */
public class InterviewId extends Identifier<UUID> {

    public InterviewId(UUID value) {
        super(value);
    }

    public static InterviewId create() {
        return new InterviewId(UUID.randomUUID());
    }

    public static InterviewId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("InterviewId 不可為 null");
        }
        return new InterviewId(value);
    }

    public static InterviewId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("InterviewId 不可為空");
        }
        return new InterviewId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InterviewId(value=" + getValue() + ")";
    }
}
