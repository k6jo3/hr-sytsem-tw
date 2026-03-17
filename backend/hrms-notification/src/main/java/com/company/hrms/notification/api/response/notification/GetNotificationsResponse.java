package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知列表查詢回應（管理員用）
 * <p>
 * 提供根路徑列表端點回應，包含所有通知資料與分頁資訊
 * </p>
 *
 * @author Claude
 * @since 2026-03-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知列表查詢回應")
public class GetNotificationsResponse {

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
     * 通知項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "通知項目")
    public static class NotificationItem {

        @Schema(description = "通知 ID", example = "ntf-001")
        private String notificationId;

        @Schema(description = "收件人 ID", example = "EMP001")
        private String recipientId;

        @Schema(description = "通知標題", example = "請假申請已核准")
        private String title;

        @Schema(description = "通知內容", example = "您的特休假申請已核准")
        private String content;

        @Schema(description = "通知類型", example = "APPROVAL_RESULT")
        private String notificationType;

        @Schema(description = "優先級", example = "NORMAL")
        private String priority;

        @Schema(description = "通知狀態", example = "SENT")
        private String status;

        @Schema(description = "是否已讀", example = "false")
        private Boolean isRead;

        @Schema(description = "建立時間")
        private LocalDateTime createdAt;

        @Schema(description = "發送時間")
        private LocalDateTime sentAt;

        @Schema(description = "已讀時間")
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

        @Schema(description = "當前頁碼", example = "1")
        private Integer currentPage;

        @Schema(description = "每頁筆數", example = "20")
        private Integer pageSize;

        @Schema(description = "總筆數", example = "50")
        private Long totalItems;

        @Schema(description = "總頁數", example = "3")
        private Integer totalPages;
    }
}
