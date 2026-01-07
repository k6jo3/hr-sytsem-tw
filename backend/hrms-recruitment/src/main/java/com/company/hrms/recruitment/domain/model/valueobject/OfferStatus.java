package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * Offer 狀態
 */
public enum OfferStatus {
    /**
     * 待處理 - 尚未回覆
     */
    PENDING("待處理"),

    /**
     * 已接受
     */
    ACCEPTED("已接受"),

    /**
     * 已拒絕
     */
    REJECTED("已拒絕"),

    /**
     * 已過期
     */
    EXPIRED("已過期"),

    /**
     * 已撤回
     */
    WITHDRAWN("已撤回");

    private final String displayName;

    OfferStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
