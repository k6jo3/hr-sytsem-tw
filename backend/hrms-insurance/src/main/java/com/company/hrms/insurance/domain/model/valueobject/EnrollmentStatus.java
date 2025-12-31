package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 加退保狀態枚舉
 */
public enum EnrollmentStatus {
    /**
     * 待加保
     */
    PENDING("待加保"),

    /**
     * 已加保
     */
    ACTIVE("已加保"),

    /**
     * 已退保
     */
    WITHDRAWN("已退保");

    private final String displayName;

    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
