package com.company.hrms.iam.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * PermissionId 值物件
 * 封裝權限 ID
 */
@Getter
@EqualsAndHashCode
public class PermissionId {

    private final String value;

    /**
     * 建構 PermissionId 值物件
     * @param value 權限 ID
     */
    public PermissionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PermissionId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 PermissionId
     * @return 新的 PermissionId
     */
    public static PermissionId generate() {
        return new PermissionId(UUID.randomUUID().toString());
    }

    /**
     * 從字串建立 PermissionId
     * @param value 權限 ID 字串
     * @return PermissionId
     */
    public static PermissionId of(String value) {
        return new PermissionId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
