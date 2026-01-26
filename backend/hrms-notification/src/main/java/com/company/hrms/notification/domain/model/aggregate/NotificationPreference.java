package com.company.hrms.notification.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.PreferenceId;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 通知偏好設定聚合根
 * <p>
 * 管理使用者的通知偏好設定，包含渠道開關與靜音時段
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationPreference extends AggregateRoot<PreferenceId> {

    /**
     * 員工 ID（唯一）
     */
    private String employeeId;

    /**
     * Email 通知是否啟用
     */
    private boolean emailEnabled;

    /**
     * 推播通知是否啟用
     */
    private boolean pushEnabled;

    /**
     * 系統內通知是否啟用
     */
    private boolean inAppEnabled;

    /**
     * Teams 通知是否啟用
     */
    private boolean teamsEnabled;

    /**
     * LINE 通知是否啟用
     */
    private boolean lineEnabled;

    /**
     * 靜音時段設定
     */
    private QuietHours quietHours;

    /**
     * Email 地址
     */
    private String emailAddress;

    /**
     * 推播裝置 Token 列表
     */
    private List<String> pushTokens;

    /**
     * LINE User ID
     */
    private String lineUserId;

    /**
     * Teams Webhook URL
     */
    private String teamsWebhookUrl;

    /**
     * 建立者
     */
    private String createdBy;

    /**
     * 最後更新者
     */
    private String updatedBy;

    /**
     * 版本號 (樂觀鎖)
     */
    private Long version;

    /**
     * 軟刪除標記
     */
    private Boolean isDeleted;

    /**
     * 私有建構子，強制使用 Factory Method
     */
    public NotificationPreference(PreferenceId id) {
        super(id);
        this.pushTokens = new ArrayList<>();
        this.isDeleted = false;
    }

    /**
     * 建立預設偏好設定 (Factory Method)
     *
     * @param employeeId 員工 ID
     * @return NotificationPreference 實例
     */
    public static NotificationPreference createDefault(String employeeId) {
        Objects.requireNonNull(employeeId, "員工 ID 不可為空");

        NotificationPreference preference = new NotificationPreference(PreferenceId.generate());
        preference.employeeId = employeeId;
        preference.emailEnabled = true;
        preference.pushEnabled = true;
        preference.inAppEnabled = true;
        preference.teamsEnabled = true;
        preference.lineEnabled = true;
        preference.quietHours = QuietHours.defaultQuietHours();

        return preference;
    }

    /**
     * 建立自訂偏好設定 (Factory Method)
     *
     * @param employeeId   員工 ID
     * @param emailEnabled Email 是否啟用
     * @param pushEnabled  推播是否啟用
     * @param inAppEnabled 系統內通知是否啟用
     * @param quietHours   靜音時段
     * @return NotificationPreference 實例
     */
    public static NotificationPreference create(
            String employeeId,
            boolean emailEnabled,
            boolean pushEnabled,
            boolean inAppEnabled,
            QuietHours quietHours) {

        Objects.requireNonNull(employeeId, "員工 ID 不可為空");

        NotificationPreference preference = new NotificationPreference(PreferenceId.generate());
        preference.employeeId = employeeId;
        preference.emailEnabled = emailEnabled;
        preference.pushEnabled = pushEnabled;
        preference.inAppEnabled = inAppEnabled;
        preference.teamsEnabled = true;
        preference.lineEnabled = true;
        preference.quietHours = quietHours != null ? quietHours : QuietHours.disabled();

        return preference;
    }

    /**
     * 更新渠道偏好設定
     *
     * @param emailEnabled  Email 是否啟用
     * @param pushEnabled   推播是否啟用
     * @param inAppEnabled  系統內通知是否啟用
     * @param teamsEnabled  Teams 是否啟用
     * @param lineEnabled   LINE 是否啟用
     */
    public void updateChannelPreferences(
            boolean emailEnabled,
            boolean pushEnabled,
            boolean inAppEnabled,
            boolean teamsEnabled,
            boolean lineEnabled) {

        this.emailEnabled = emailEnabled;
        this.pushEnabled = pushEnabled;
        this.inAppEnabled = inAppEnabled;
        this.teamsEnabled = teamsEnabled;
        this.lineEnabled = lineEnabled;
        touch();
    }

    /**
     * 更新靜音時段
     *
     * @param quietHours 靜音時段
     */
    public void updateQuietHours(QuietHours quietHours) {
        this.quietHours = quietHours != null ? quietHours : QuietHours.disabled();
        touch();
    }

    /**
     * 過濾通知渠道
     * <p>
     * 根據使用者偏好過濾請求的渠道列表
     * </p>
     *
     * @param requestedChannels 請求的渠道列表
     * @return 過濾後的渠道列表
     */
    public List<NotificationChannel> filterChannels(List<NotificationChannel> requestedChannels) {
        if (requestedChannels == null || requestedChannels.isEmpty()) {
            return List.of(NotificationChannel.IN_APP);
        }

        List<NotificationChannel> filtered = new ArrayList<>();

        for (NotificationChannel channel : requestedChannels) {
            if (isChannelEnabled(channel)) {
                filtered.add(channel);
            }
        }

        // 至少保留系統內通知
        if (filtered.isEmpty() && inAppEnabled) {
            filtered.add(NotificationChannel.IN_APP);
        }

        return filtered;
    }

    /**
     * 檢查指定渠道是否啟用
     *
     * @param channel 渠道
     * @return true 表示啟用
     */
    private boolean isChannelEnabled(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailEnabled;
            case PUSH -> pushEnabled;
            case IN_APP -> inAppEnabled;
            case TEAMS -> teamsEnabled;
            case LINE -> lineEnabled;
        };
    }

    /**
     * 檢查當前時間是否在靜音時段內
     *
     * @return true 表示在靜音時段內
     */
    public boolean isInQuietHours() {
        if (quietHours == null) {
            return false;
        }
        return quietHours.isInQuietHours(java.time.LocalTime.now());
    }

    // ========== Getters ==========

    public String getEmployeeId() {
        return employeeId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public boolean isTeamsEnabled() {
        return teamsEnabled;
    }

    public boolean isLineEnabled() {
        return lineEnabled;
    }

    public QuietHours getQuietHours() {
        return quietHours;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<String> getPushTokens() {
        return pushTokens;
    }

    public void setPushTokens(List<String> pushTokens) {
        this.pushTokens = pushTokens;
    }

    public String getLineUserId() {
        return lineUserId;
    }

    public void setLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public String getTeamsWebhookUrl() {
        return teamsWebhookUrl;
    }

    public void setTeamsWebhookUrl(String teamsWebhookUrl) {
        this.teamsWebhookUrl = teamsWebhookUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setInAppEnabled(boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public void setTeamsEnabled(boolean teamsEnabled) {
        this.teamsEnabled = teamsEnabled;
    }

    public void setLineEnabled(boolean lineEnabled) {
        this.lineEnabled = lineEnabled;
    }

    public void setQuietHours(QuietHours quietHours) {
        this.quietHours = quietHours;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
