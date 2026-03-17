package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 保險類型枚舉
 *
 * [2026-03-17 更新] 新增 OCCUPATIONAL_ACCIDENT（職災保險）、EMPLOYMENT_INSURANCE（就業保險）
 * 新增 isRequiredForSenior() / isRequiredForNonSenior() 方法，用於 65 歲以上年齡判斷
 */
public enum InsuranceType {
    /**
     * 勞保（普通事故）
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
     * 職災保險
     * [2026-03-17 新增] 勞工職業災害保險，雇主 100% 負擔
     */
    OCCUPATIONAL_ACCIDENT("職災保險"),

    /**
     * 就業保險
     * [2026-03-17 新增] 就業保險，65 歲以上免投保
     */
    EMPLOYMENT_INSURANCE("就業保險"),

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
     * 是否為法定保險（勞保/健保/勞退/職災/就業保險）
     */
    public boolean isStatutory() {
        return this == LABOR || this == HEALTH || this == PENSION
                || this == OCCUPATIONAL_ACCIDENT || this == EMPLOYMENT_INSURANCE;
    }

    /**
     * 是否為團體保險
     */
    public boolean isGroupInsurance() {
        return this == GROUP_LIFE || this == GROUP_ACCIDENT || this == GROUP_MEDICAL;
    }

    /**
     * 65 歲以上（含）勞工必須投保的險種
     * 勞保普通事故與就業保險免投保，但職災、健保、勞退仍須投保
     *
     * @return true 表示 65 歲以上仍須投保此險種
     */
    public boolean isRequiredForSenior() {
        return this == OCCUPATIONAL_ACCIDENT || this == HEALTH || this == PENSION;
    }

    /**
     * 未滿 65 歲勞工必須投保的險種
     * 包含勞保、就業保險、職災、健保、勞退
     *
     * @return true 表示未滿 65 歲須投保此險種
     */
    public boolean isRequiredForNonSenior() {
        return this == LABOR || this == EMPLOYMENT_INSURANCE
                || this == OCCUPATIONAL_ACCIDENT || this == HEALTH || this == PENSION;
    }
}
