package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 面試類型
 */
public enum InterviewType {
    /**
     * 電話面試
     */
    PHONE("電話面試"),

    /**
     * 視訊面試
     */
    VIDEO("視訊面試"),

    /**
     * 現場面試
     */
    ONSITE("現場面試"),

    /**
     * 技術面試
     */
    TECHNICAL("技術面試");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
