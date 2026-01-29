package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 發送通知回應
 *
 * @author Claude
 * @since 2025-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "發送通知回應")
public class SendNotificationResponse {

    /**
     * 通知 ID
     */
    @Schema(description = "通知 ID", example = "ntf-001")
    private String notificationId;

    /**
     * 收件人 ID
     */
    @Schema(description = "收件人 ID", example = "emp-001")
    private String recipientId;

    /**
     * 通知標題
     */
    @Schema(description = "通知標題", example = "請假申請待審核")
    private String title;

    /**
     * 發送狀態
     */
    @Schema(description = "發送狀態", example = "SENT")
    private String status;

    /**
     * 發送渠道
     */
    @Schema(description = "發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> channels;

    /**
     * 發送時間
     */
    @Schema(description = "發送時間", example = "2025-12-30T10:00:00")
    private LocalDateTime sentAt;

    /**
     * 業務連結 URL
     */
    @Schema(description = "業務連結 URL", example = "/attendance/leave/applications/leave-001")
    private String businessUrl;

    /**
     * 各渠道發送結果
     */
    @Schema(description = "各渠道發送結果")
    private List<ChannelResult> channelResults;

    /**
     * 渠道發送結果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "渠道發送結果")
    public static class ChannelResult {

        /**
         * 渠道
         */
        @Schema(description = "渠道", example = "IN_APP")
        private String channel;

        /**
         * 發送狀態
         */
        @Schema(description = "發送狀態", example = "SUCCESS")
        private String status;

        /**
         * 失敗原因（若失敗）
         */
        @Schema(description = "失敗原因", example = "Email 地址無效")
        private String failureReason;
    }
}
