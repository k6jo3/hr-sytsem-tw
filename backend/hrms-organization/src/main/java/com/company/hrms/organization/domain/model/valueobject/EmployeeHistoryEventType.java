package com.company.hrms.organization.domain.model.valueobject;

/**
 * 員工人事歷程事件類型列舉
 */
public enum EmployeeHistoryEventType {
    /**
     * 到職
     */
    ONBOARDING("到職"),

    /**
     * 試用期轉正
     */
    PROBATION_PASSED("試用期轉正"),

    /**
     * 部門調動
     */
    DEPARTMENT_TRANSFER("部門調動"),

    /**
     * 職務異動
     */
    JOB_CHANGE("職務異動"),

    /**
     * 升遷
     */
    PROMOTION("升遷"),

    /**
     * 調薪
     */
    SALARY_ADJUSTMENT("調薪"),

    /**
     * 離職
     */
    TERMINATION("離職"),

    /**
     * 復職
     */
    REHIRE("復職");

    private final String displayName;

    EmployeeHistoryEventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
