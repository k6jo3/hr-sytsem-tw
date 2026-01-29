package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢我的通知列表回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢我的通知列表回應")
public class GetMyNotificationsResponse {

    /**
     * 通知列表
     */
    @Schema(description = "通知列表")
    private List<NotificationItem> items;

    /**
     * 分頁資訊
     */
    @Schema(description = "分頁資訊")
    private Pagination pagination;

    /**
     * 摘要資訊
     */
    @Schema(description = "摘要資訊")
    private Summary summary;

    /**
     * 通知項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "通知項目")
    public static class NotificationItem {

        /**
         * 通知 ID
         */
        @Schema(description = "通知 ID", example = "ntf-001")
        private String notificationId;

        /**
         * 通知標題
         */
        @Schema(description = "通知標題", example = "請假申請已核准")
        private String title;

        /**
         * 通知內容
         */
        @Schema(description = "通知內容", example = "您的特休假申請已核准")
        private String content;

        /**
         * 通知類型
         */
        @Schema(description = "通知類型", example = "APPROVAL_RESULT")
        private String notificationType;

        /**
         * 優先級
         */
        @Schema(description = "優先級", example = "NORMAL")
        private String priority;

        /**
         * 通知狀態
         */
        @Schema(description = "通知狀態", example = "SENT")
        private String status;

        /**
         * 是否已讀
         */
        @Schema(description = "是否已讀", example = "false")
        private Boolean isRead;

        /**
         * 關聯業務類型
         */
        @Schema(description = "關聯業務類型", example = "LEAVE_APPLICATION")
        private String businessType;

        /**
         * 關聯業務 ID
         */
        @Schema(description = "關聯業務 ID", example = "leave-001")
        private String businessId;

        /**
         * 業務詳情連結
         */
        @Schema(description = "業務詳情連結", example = "/attendance/leave/applications/leave-001")
        private String businessUrl;

        /**
         * 建立時間
         */
        @Schema(description = "建立時間", example = "2025-12-30T10:00:00")
        private LocalDateTime createdAt;

        /**
         * 已讀時間
         */
        @Schema(description = "已讀時間", example = "2025-12-30T10:30:00")
        private LocalDateTime readAt;
    }

    /**
     * 分頁資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分頁資訊")
    public static class Pagination {

        /**
         * 當前頁碼
         */
        @Schema(description = "當前頁碼", example = "1")
        private Integer currentPage;

        /**
         * 每頁筆數
         */
        @Schema(description = "每頁筆數", example = "20")
        private Integer pageSize;

        /**
         * 總筆數
         */
        @Schema(description = "總筆數", example = "25")
        private Long totalItems;

        /**
         * 總頁數
         */
        @Schema(description = "總頁數", example = "2")
        private Integer totalPages;
    }

    /**
     * 摘要資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "摘要資訊")
    public static class Summary {

        /**
         * 未讀通知數量
         */
        @Schema(description = "未讀通知數量", example = "5")
        private Long totalUnread;
    }
}
