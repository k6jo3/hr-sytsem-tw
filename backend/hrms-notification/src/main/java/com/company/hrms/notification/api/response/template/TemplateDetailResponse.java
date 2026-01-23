package com.company.hrms.notification.api.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 範本詳情回應
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "範本詳情回應")
public class TemplateDetailResponse {

    /**
     * 範本 ID
     */
    @Schema(description = "範本 ID", example = "tpl-001")
    private String templateId;

    /**
     * 範本代碼
     */
    @Schema(description = "範本代碼", example = "LEAVE_APPROVED")
    private String templateCode;

    /**
     * 範本名稱
     */
    @Schema(description = "範本名稱", example = "請假申請核准通知")
    private String templateName;

    /**
     * 範本說明
     */
    @Schema(description = "範本說明")
    private String description;

    /**
     * Email 主旨
     */
    @Schema(description = "Email 主旨", example = "【請假核准】您的請假申請已核准")
    private String subject;

    /**
     * 範本內容
     */
    @Schema(description = "範本內容")
    private String body;

    /**
     * 通知類型
     */
    @Schema(description = "通知類型", example = "APPROVAL_RESULT")
    private String notificationType;

    /**
     * 預設優先級
     */
    @Schema(description = "預設優先級", example = "NORMAL")
    private String defaultPriority;

    /**
     * 預設發送渠道
     */
    @Schema(description = "預設發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> defaultChannels;

    /**
     * 範本變數定義
     */
    @Schema(description = "範本變數定義")
    private Map<String, String> variables;

    /**
     * 是否啟用
     */
    @Schema(description = "是否啟用", example = "true")
    private Boolean isActive;

    /**
     * 建立時間
     */
    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    /**
     * 建立者
     */
    @Schema(description = "建立者")
    private String createdBy;

    /**
     * 更新時間
     */
    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updatedBy;
}
