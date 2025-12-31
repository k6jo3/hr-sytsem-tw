package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 保險類型枚舉
 */
public enum InsuranceType {
    /**
     * 勞保
     */
    LABOR("勞保"),

    /**
     * 健保
     */
    HEALTH("健保"),

    /**
     * 勞退
     */
    PENSION("勞退");

    private final String displayName;

    InsuranceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
