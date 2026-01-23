package com.company.hrms.notification.domain.model.valueobject;

/**
 * 通知類型列舉
 * <p>
 * 定義所有支援的通知類型，用於分類通知的業務場景
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public enum NotificationType {

    /**
     * 審核請求
     * <p>常見場景：請假/加班申請待審核</p>
     */
    APPROVAL_REQUEST("審核請求", "請假/加班申請待審核"),

    /**
     * 審核結果
     * <p>常見場景：請假/加班申請通過/駁回</p>
     */
    APPROVAL_RESULT("審核結果", "請假/加班申請通過/駁回"),

    /**
     * 提醒
     * <p>常見場景：合約到期、證照到期、生日</p>
     */
    REMINDER("提醒", "合約到期、證照到期、生日"),

    /**
     * 公告
     * <p>常見場景：系統公告、活動通知</p>
     */
    ANNOUNCEMENT("公告", "系統公告、活動通知"),

    /**
     * 警示
     * <p>常見場景：異常警告、緊急通知</p>
     */
    ALERT("警示", "異常警告、緊急通知");

    private final String displayName;
    private final String commonScenario;

    NotificationType(String displayName, String commonScenario) {
        this.displayName = displayName;
        this.commonScenario = commonScenario;
    }

    /**
     * 取得類型顯示名稱
     *
     * @return 顯示名稱
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 取得常見場景說明
     *
     * @return 常見場景
     */
    public String getCommonScenario() {
        return commonScenario;
    }
}