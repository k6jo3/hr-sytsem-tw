package com.company.hrms.organization.domain.model.valueobject;

/**
 * 部門狀態列舉
 */
public enum DepartmentStatus {
    /**
     * 啟用中
     */
    ACTIVE("啟用"),

    /**
     * 已停用
     */
    INACTIVE("停用");

    private final String displayName;

    DepartmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
