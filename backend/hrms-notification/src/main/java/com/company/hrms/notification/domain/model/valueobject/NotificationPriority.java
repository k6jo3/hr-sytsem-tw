package com.company.hrms.notification.domain.model.valueobject;

/**
 * 通知優先級列舉
 * <p>
 * 定義通知的優先級，影響發送行為和靜音時段處理
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public enum NotificationPriority {

    /**
     * 低優先級
     * <p>處理方式：不受靜音時段限制影響</p>
     */
    LOW("低", "不受靜音時段限制影響", false),

    /**
     * 一般優先級
     * <p>處理方式：預設，遵守靜音時段</p>
     */
    NORMAL("一般", "預設，遵守靜音時段", true),

    /**
     * 高優先級
     * <p>處理方式：即時發送，不等待</p>
     */
    HIGH("高", "即時發送，不等待", false),

    /**
     * 緊急優先級
     * <p>處理方式：忽略所有限制立即發送</p>
     */
    URGENT("緊急", "忽略所有限制立即發送", false);

    private final String displayName;
    private final String processingRule;
    private final boolean respectQuietHours;

    NotificationPriority(String displayName, String processingRule, boolean respectQuietHours) {
        this.displayName = displayName;
        this.processingRule = processingRule;
        this.respectQuietHours = respectQuietHours;
    }

    /**
     * 取得優先級顯示名稱
     *
     * @return 顯示名稱
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 取得處理規則說明
     *
     * @return 處理規則
     */
    public String getProcessingRule() {
        return processingRule;
    }

    /**
     * 是否遵守靜音時段
     *
     * @return true 表示遵守靜音時段
     */
    public boolean shouldRespectQuietHours() {
        return respectQuietHours;
    }

    /**
     * 是否為緊急通知
     *
     * @return true 表示緊急通知
     */
    public boolean isUrgent() {
        return this == URGENT;
    }
}
