package com.company.hrms.notification.api.response.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批次發送通知回應
 *
 * @author Claude
 * @since 2025-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批次發送通知回應")
public class SendBatchNotificationResponse {

    /**
     * 總收件人數量
     */
    @Schema(description = "總收件人數量", example = "100")
    private Integer totalRecipients;

    /**
     * 成功發送數量
     */
    @Schema(description = "成功發送數量", example = "98")
    private Integer successCount;

    /**
     * 失敗數量
     */
    @Schema(description = "失敗數量", example = "2")
    private Integer failureCount;

    /**
     * 發送結果列表
     */
    @Schema(description = "發送結果列表")
    private List<BatchResult> results;

    /**
     * 失敗的收件人列表
     */
    @Schema(description = "失敗的收件人列表")
    private List<FailedRecipient> failedRecipients;

    /**
     * 批次發送結果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "批次發送結果")
    public static class BatchResult {

        /**
         * 收件人 ID
         */
        @Schema(description = "收件人 ID", example = "emp-001")
        private String recipientId;

        /**
         * 通知 ID
         */
        @Schema(description = "通知 ID", example = "ntf-001")
        private String notificationId;

        /**
         * 發送狀態
         */
        @Schema(description = "發送狀態", example = "SENT")
        private String status;
    }

    /**
     * 失敗的收件人
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "失敗的收件人")
    public static class FailedRecipient {

        /**
         * 收件人 ID
         */
        @Schema(description = "收件人 ID", example = "emp-002")
        private String recipientId;

        /**
         * 失敗原因
         */
        @Schema(description = "失敗原因", example = "收件人不存在")
        private String reason;
    }
}
