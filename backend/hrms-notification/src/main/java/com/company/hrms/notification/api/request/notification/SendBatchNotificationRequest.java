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
 * 批次發送通知請求
 * <p>
 * 支援兩種收件人指定方式：
 * <ol>
 * <li>直接指定收件人 ID 列表 (recipientIds)</li>
 * <li>使用過濾條件查詢收件人 (recipientFilter)</li>
 * </ol>
 * 批次上限：500 人
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批次發送通知請求")
public class SendBatchNotificationRequest {

    /**
     * 收件人 ID 列表（直接指定方式）
     */
    @Schema(description = "收件人 ID 列表", example = "[\"emp-001\", \"emp-002\", \"emp-003\"]")
    private List<String> recipientIds;

    /**
     * 收件人過濾條件（查詢方式）
     */
    @Schema(description = "收件人過濾條件")
    private RecipientFilter recipientFilter;

    /**
     * 通知標題
     */
    @NotBlank(message = "通知標題不可為空")
    @Size(min = 1, max = 200, message = "通知標題長度必須在 1-200 字元之間")
    @Schema(description = "通知標題", example = "系統維護通知")
    private String title;

    /**
     * 通知內容（無範本時必填）
     */
    @Size(max = 2000, message = "通知內容最多 2000 字元")
    @Schema(description = "通知內容", example = "系統將於今晚 22:00-24:00 進行維護，期間無法使用。")
    private String content;

    /**
     * 通知類型
     */
    @NotBlank(message = "通知類型不可為空")
    @Schema(description = "通知類型", example = "SYSTEM_NOTICE")
    private String notificationType;

    /**
     * 發送渠道列表（預設 IN_APP）
     */
    @Schema(description = "發送渠道列表", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> channels;

    /**
     * 優先級（預設 NORMAL）
     */
    @Schema(description = "優先級", example = "HIGH")
    private String priority;

    /**
     * 範本代碼
     */
    @Schema(description = "範本代碼", example = "SYSTEM_MAINTENANCE")
    private String templateCode;

    /**
     * 範本變數
     */
    @Schema(description = "範本變數")
    private Map<String, Object> templateVariables;

    /**
     * 關聯業務類型
     */
    @Schema(description = "關聯業務類型", example = "SYSTEM_MAINTENANCE")
    private String businessType;

    /**
     * 關聯業務 ID
     */
    @Schema(description = "關聯業務 ID", example = "maint-001")
    private String businessId;

    /**
     * 業務詳情連結
     */
    @Schema(description = "業務詳情連結", example = "/system/maintenance/maint-001")
    private String businessUrl;

    /**
     * 收件人過濾條件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "收件人過濾條件")
    public static class RecipientFilter {

        /**
         * 部門 ID 列表
         */
        @Schema(description = "部門 ID 列表", example = "[\"dept-001\", \"dept-002\"]")
        private List<String> departmentIds;

        /**
         * 角色 ID 列表
         */
        @Schema(description = "角色 ID 列表", example = "[\"role-001\"]")
        private List<String> roleIds;

        /**
         * 員工狀態列表
         */
        @Schema(description = "員工狀態列表", example = "[\"ACTIVE\"]")
        private List<String> employeeStatuses;
    }
}
