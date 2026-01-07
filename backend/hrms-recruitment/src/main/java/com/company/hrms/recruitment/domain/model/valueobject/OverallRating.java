package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 面試評價
 */
public enum OverallRating {
    /**
     * 強烈推薦錄取
     */
    STRONG_HIRE("強烈推薦"),

    /**
     * 推薦錄取
     */
    HIRE("推薦"),

    /**
     * 不推薦錄取
     */
    NO_HIRE("不推薦"),

    /**
     * 強烈不推薦錄取
     */
    STRONG_NO_HIRE("強烈不推薦");

    private final String displayName;

    OverallRating(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
