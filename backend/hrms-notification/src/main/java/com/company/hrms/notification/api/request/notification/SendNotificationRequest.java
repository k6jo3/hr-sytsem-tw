package com.company.hrms.notification.api.request.notification;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 發送通知請求
 *
 * @author Claude
 * @since 2025-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "發送通知請求")
public class SendNotificationRequest {

    /**
     * 收件人員工 ID
     */
    @NotBlank(message = "收件人 ID 不可為空")
    @Schema(description = "收件人員工 ID", example = "emp-001")
    private String recipientId;

    /**
     * 通知標題
     */
    @NotBlank(message = "通知標題不可為空")
    @Size(min = 1, max = 200, message = "通知標題長度必須在 1-200 字元之間")
    @Schema(description = "通知標題", example = "請假申請待審核")
    private String title;

    /**
     * 通知內容（無範本時必填）
     */
    @Size(max = 2000, message = "通知內容最多 2000 字元")
    @Schema(description = "通知內容", example = "員工張三申請特休假2天（2025/12/20-2025/12/21），請您審核。")
    private String content;

    /**
     * 通知類型
     */
    @NotBlank(message = "通知類型不可為空")
    @Schema(description = "通知類型", example = "APPROVAL_REQUEST")
    private String notificationType;

    /**
     * 發送渠道（預設 IN_APP）
     */
    @Schema(description = "發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> channels;

    /**
     * 優先級（預設 NORMAL）
     */
    @Schema(description = "優先級", example = "NORMAL", defaultValue = "NORMAL")
    private String priority;

    /**
     * 使用的通知範本代碼
     */
    @Schema(description = "使用的通知範本代碼", example = "LEAVE_APPROVAL_REQUEST")
    private String templateCode;

    /**
     * 範本變數
     */
    @Schema(description = "範本變數")
    private Map<String, Object> templateVariables;

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
}
