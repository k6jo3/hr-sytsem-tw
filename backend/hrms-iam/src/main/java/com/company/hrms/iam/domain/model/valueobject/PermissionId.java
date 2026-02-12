package com.company.hrms.iam.domain.model.valueobject;

import java.util.UUID;

/**
 * PermissionId 值物件
 * 封裝權限 ID
 */
public class PermissionId {

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PermissionId that = (PermissionId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private final String value;

    /**
     * 建構 PermissionId 值物件
     * 
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
     * 
     * @return 新的 PermissionId
     */
    public static PermissionId generate() {
        return new PermissionId(UUID.randomUUID().toString());
    }

    /**
     * 從字串建立 PermissionId
     * 
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
