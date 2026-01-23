package com.company.hrms.notification.api.request.preference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新通知偏好設定請求
 * <p>
 * 使用者調整個人通知渠道偏好與靜音時段設定
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新通知偏好設定請求")
public class UpdateNotificationPreferenceRequest {

    /**
     * 渠道偏好設定
     */
    @Schema(description = "渠道偏好設定")
    private ChannelPreferences channels;

    /**
     * 靜音時段設定
     */
    @Schema(description = "靜音時段設定")
    private QuietHoursSettings quietHours;

    /**
     * 渠道偏好設定
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "渠道偏好設定")
    public static class ChannelPreferences {

        @Schema(description = "系統內通知是否啟用", example = "true")
        private Boolean inAppEnabled;

        @Schema(description = "Email 通知是否啟用", example = "true")
        private Boolean emailEnabled;

        @Schema(description = "推播通知是否啟用", example = "false")
        private Boolean pushEnabled;

        @Schema(description = "Teams 通知是否啟用", example = "false")
        private Boolean teamsEnabled;

        @Schema(description = "LINE 通知是否啟用", example = "false")
        private Boolean lineEnabled;
    }

    /**
     * 靜音時段設定
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "靜音時段設定")
    public static class QuietHoursSettings {

        @Schema(description = "是否啟用靜音時段", example = "true")
        private Boolean enabled;

        @Schema(description = "靜音開始時間 (HH:mm)", example = "22:00")
        private String startTime;

        @Schema(description = "靜音結束時間 (HH:mm)", example = "08:00")
        private String endTime;
    }
}
