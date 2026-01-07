package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 職缺狀態
 */
public enum JobStatus {
    /**
     * 草稿 - 尚未發布
     */
    DRAFT("草稿"),

    /**
     * 開放中 - 正在招募
     */
    OPEN("開放中"),

    /**
     * 已關閉 - 手動關閉招募
     */
    CLOSED("已關閉"),

    /**
     * 已滿額 - 已招滿
     */
    FILLED("已滿額");

    private final String displayName;

    JobStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 檢查是否可以轉換到目標狀態
     */
    public boolean canTransitionTo(JobStatus targetStatus) {
        switch (this) {
            case DRAFT:
                return targetStatus == OPEN;
            case OPEN:
                return targetStatus == CLOSED || targetStatus == FILLED;
            case CLOSED:
            case FILLED:
                return false; // 終態不可轉換
            default:
                return false;
        }
    }
}
