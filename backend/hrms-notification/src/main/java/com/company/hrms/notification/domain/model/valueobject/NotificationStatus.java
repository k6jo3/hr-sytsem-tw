package com.company.hrms.notification.domain.model.valueobject;

/**
 * 通知狀態列舉
 * <p>
 * 定義通知的生命週期狀態
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public enum NotificationStatus {

    /**
     * 待發送
     * <p>通知已建立但尚未發送</p>
     */
    PENDING("待發送"),

    /**
     * 已發送
     * <p>通知已成功發送至所有渠道</p>
     */
    SENT("已發送"),

    /**
     * 已讀
     * <p>收件人已讀取通知</p>
     */
    READ("已讀"),

    /**
     * 發送失敗
     * <p>通知發送失敗</p>
     */
    FAILED("發送失敗");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 取得狀態顯示名稱
     *
     * @return 顯示名稱
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為已讀狀態
     *
     * @return true 表示已讀
     */
    public boolean isRead() {
        return this == READ;
    }

    /**
     * 是否為失敗狀態
     *
     * @return true 表示失敗
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * 是否為成功發送狀態
     *
     * @return true 表示已發送或已讀
     */
    public boolean isSuccessfullySent() {
        return this == SENT || this == READ;
    }
}
