package com.company.hrms.organization.domain.model.valueobject;

/**
 * 婚姻狀況列舉
 */
public enum MaritalStatus {
    /**
     * 未婚
     */
    SINGLE("未婚"),

    /**
     * 已婚
     */
    MARRIED("已婚"),

    /**
     * 離婚
     */
    DIVORCED("離婚"),

    /**
     * 喪偶
     */
    WIDOWED("喪偶");

    private final String displayName;

    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
