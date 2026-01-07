package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 雇用類型
 */
public enum EmploymentType {
    /**
     * 全職
     */
    FULL_TIME("全職"),

    /**
     * 兼職
     */
    PART_TIME("兼職"),

    /**
     * 約聘
     */
    CONTRACT("約聘");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
