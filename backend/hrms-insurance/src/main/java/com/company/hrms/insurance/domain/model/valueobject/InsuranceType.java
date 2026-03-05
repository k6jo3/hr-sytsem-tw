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
    PENSION("勞退"),

    /**
     * 團體壽險
     */
    GROUP_LIFE("團體壽險"),

    /**
     * 團體傷害險
     */
    GROUP_ACCIDENT("團體傷害險"),

    /**
     * 團體醫療險
     */
    GROUP_MEDICAL("團體醫療險");

    private final String displayName;

    InsuranceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為法定保險（勞保/健保/勞退）
     */
    public boolean isStatutory() {
        return this == LABOR || this == HEALTH || this == PENSION;
    }

    /**
     * 是否為團體保險
     */
    public boolean isGroupInsurance() {
        return this == GROUP_LIFE || this == GROUP_ACCIDENT || this == GROUP_MEDICAL;
    }
}
