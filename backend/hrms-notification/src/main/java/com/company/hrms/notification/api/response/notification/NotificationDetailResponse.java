package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知詳情回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知詳情回應")
public class NotificationDetailResponse {

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
    @Schema(description = "通知內容", example = "您的特休假申請（2025/12/20-2025/12/21）已核准。")
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
    @Schema(description = "通知狀態", example = "READ")
    private String status;

    /**
     * 發送渠道
     */
    @Schema(description = "發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> channels;

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
     * 發送時間
     */
    @Schema(description = "發送時間", example = "2025-12-30T10:00:05")
    private LocalDateTime sentAt;

    /**
     * 已讀時間
     */
    @Schema(description = "已讀時間", example = "2025-12-30T10:30:00")
    private LocalDateTime readAt;
}
