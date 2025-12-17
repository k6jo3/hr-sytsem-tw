package com.company.hrms.organization.domain.model.valueobject;

/**
 * 雇用類型列舉
 */
public enum EmploymentType {
    /**
     * 正職
     */
    FULL_TIME("正職"),

    /**
     * 約聘
     */
    CONTRACT("約聘"),

    /**
     * 兼職
     */
    PART_TIME("兼職"),

    /**
     * 實習
     */
    INTERN("實習");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為正式員工 (正職或約聘)
     * @return 是否為正式員工
     */
    public boolean isRegularEmployee() {
        return this == FULL_TIME || this == CONTRACT;
    }
}
