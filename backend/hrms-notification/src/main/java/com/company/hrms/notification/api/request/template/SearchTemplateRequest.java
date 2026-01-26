package com.company.hrms.notification.api.request.template;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢通知範本請求
 * <p>
 * 使用 @QueryFilter 註解自動組裝查詢條件 (Fluent-Query-Engine)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢通知範本請求")
public class SearchTemplateRequest {

    /**
     * 關鍵字搜尋（範本代碼或名稱）
     */
    @QueryFilter(property = "template_code", operator = Operator.LIKE)
    @Schema(description = "範本代碼關鍵字", example = "LEAVE")
    private String templateCodeKeyword;

    /**
     * 範本名稱關鍵字
     */
    @QueryFilter(property = "template_name", operator = Operator.LIKE)
    @Schema(description = "範本名稱關鍵字", example = "請假")
    private String templateNameKeyword;

    /**
     * 通知類型
     */
    @QueryFilter(property = "notification_type", operator = Operator.EQ)
    @Schema(description = "通知類型", example = "APPROVAL_RESULT")
    private String notificationType;

    /**
     * 是否只顯示啟用的範本
     */
    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "狀態過濾（ACTIVE/INACTIVE）", example = "ACTIVE")
    private String status;

    /**
     * 頁碼
     */
    @Schema(description = "頁碼", example = "1", defaultValue = "1")
    private Integer page;

    /**
     * 每頁筆數
     */
    @Schema(description = "每頁筆數", example = "20", defaultValue = "20")
    private Integer pageSize;
}
