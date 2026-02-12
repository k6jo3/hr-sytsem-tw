package com.company.hrms.iam.domain.model.valueobject;

import java.util.UUID;

/**
 * RoleId 值物件
 * 封裝角色 ID
 */
public class RoleId {

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleId roleId = (RoleId) o;
        return value.equals(roleId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private final String value;

    /**
     * 建構 RoleId 值物件
     * 
     * @param value 角色 ID
     */
    public RoleId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RoleId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 RoleId
     * 
     * @return 新的 RoleId
     */
    public static RoleId generate() {
        return new RoleId(UUID.randomUUID().toString());
    }

    /**
     * 從字串建立 RoleId
     * 
     * @param value 角色 ID 字串
     * @return RoleId
     */
    public static RoleId of(String value) {
        return new RoleId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
