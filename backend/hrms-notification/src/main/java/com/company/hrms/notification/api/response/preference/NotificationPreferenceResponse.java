package com.company.hrms.notification.api.response.preference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知偏好設定回應
 * <p>
 * 回傳使用者的通知偏好設定資訊
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知偏好設定回應")
public class NotificationPreferenceResponse {

    @Schema(description = "偏好設定 ID", example = "pref-001")
    private String preferenceId;

    @Schema(description = "員工 ID", example = "emp-001")
    private String employeeId;

    @Schema(description = "渠道偏好設定")
    private ChannelPreferences channels;

    @Schema(description = "靜音時段設定")
    private QuietHoursSettings quietHours;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "建立者")
    private String createdBy;

    @Schema(description = "最後更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "最後更新者")
    private String updatedBy;

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
        private boolean inAppEnabled;

        @Schema(description = "Email 通知是否啟用", example = "true")
        private boolean emailEnabled;

        @Schema(description = "推播通知是否啟用", example = "false")
        private boolean pushEnabled;

        @Schema(description = "Teams 通知是否啟用", example = "false")
        private boolean teamsEnabled;

        @Schema(description = "LINE 通知是否啟用", example = "false")
        private boolean lineEnabled;
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
        private boolean enabled;

        @Schema(description = "靜音開始時間 (HH:mm)", example = "22:00")
        private String startTime;

        @Schema(description = "靜音結束時間 (HH:mm)", example = "08:00")
        private String endTime;
    }
}
