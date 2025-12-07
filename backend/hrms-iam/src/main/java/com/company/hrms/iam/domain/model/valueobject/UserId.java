package com.company.hrms.iam.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * UserId 值物件
 * 封裝使用者 ID
 */
@Getter
@EqualsAndHashCode
public class UserId {

    private final String value;

    /**
     * 建構 UserId 值物件
     * @param value 使用者 ID
     */
    public UserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 UserId
     * @return 新的 UserId
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
