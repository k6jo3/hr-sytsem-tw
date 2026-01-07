package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 面試狀態
 */
public enum InterviewStatus {
    /**
     * 已排程
     */
    SCHEDULED("已排程"),

    /**
     * 已完成
     */
    COMPLETED("已完成"),

    /**
     * 已取消
     */
    CANCELLED("已取消");

    private final String displayName;

    InterviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
