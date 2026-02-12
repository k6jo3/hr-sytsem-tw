package com.company.hrms.iam.domain.model.valueobject;

import java.util.UUID;

/**
 * UserId 值物件
 * 封裝使用者 ID
 */
public class UserId {

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private final String value;

    /**
     * 建構 UserId 值物件
     * 
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
     * 
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
