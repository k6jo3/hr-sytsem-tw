package com.company.hrms.iam.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * RoleId 值物件
 * 封裝角色 ID
 */
@Getter
@EqualsAndHashCode
public class RoleId {

    private final String value;

    /**
     * 建構 RoleId 值物件
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
     * @return 新的 RoleId
     */
    public static RoleId generate() {
        return new RoleId(UUID.randomUUID().toString());
    }

    /**
     * 從字串建立 RoleId
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
