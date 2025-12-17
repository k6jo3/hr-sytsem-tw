package com.company.hrms.organization.domain.model.valueobject;

/**
 * 在職狀態列舉
 */
public enum EmploymentStatus {
    /**
     * 試用期
     */
    PROBATION("試用"),

    /**
     * 在職
     */
    ACTIVE("在職"),

    /**
     * 育嬰留停
     */
    PARENTAL_LEAVE("育嬰留停"),

    /**
     * 留職停薪
     */
    UNPAID_LEAVE("留職停薪"),

    /**
     * 離職
     */
    TERMINATED("離職");

    private final String displayName;

    EmploymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為活躍狀態 (可出勤)
     * @return 是否活躍
     */
    public boolean isActive() {
        return this == PROBATION || this == ACTIVE;
    }

    /**
     * 是否為留停狀態
     * @return 是否留停
     */
    public boolean isOnLeave() {
        return this == PARENTAL_LEAVE || this == UNPAID_LEAVE;
    }

    /**
     * 是否已離職
     * @return 是否離職
     */
    public boolean isTerminated() {
        return this == TERMINATED;
    }

    /**
     * 是否為試用期
     * @return 是否試用期
     */
    public boolean isProbation() {
        return this == PROBATION;
    }
}
