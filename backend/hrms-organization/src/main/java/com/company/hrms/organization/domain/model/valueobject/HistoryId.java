package com.company.hrms.organization.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 人事歷程 ID 值對象
 */
@Getter
@EqualsAndHashCode
public class HistoryId {

    private final UUID value;

    public HistoryId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("HistoryId 不可為空");
        }
        this.value = value;
    }

    public HistoryId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HistoryId 不可為空");
        }
        this.value = UUID.fromString(value);
    }

    public static HistoryId generate() {
        return new HistoryId(UUID.randomUUID());
    }

    public static HistoryId of(UUID value) {
        return new HistoryId(value);
    }

    public static HistoryId of(String value) {
        return new HistoryId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
