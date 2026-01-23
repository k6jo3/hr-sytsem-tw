package com.company.hrms.notification.api.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 更新通知範本請求
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新通知範本請求")
public class UpdateNotificationTemplateRequest {

    /**
     * 範本名稱
     */
    @Size(min = 1, max = 100, message = "範本名稱長度必須在1-100字元之間")
    @Schema(description = "範本名稱", example = "請假申請核准通知（更新版）")
    private String templateName;

    /**
     * 範本說明
     */
    @Size(max = 500, message = "範本說明長度不可超過500字元")
    @Schema(description = "範本說明")
    private String description;

    /**
     * Email 主旨範本
     */
    @Size(max = 200, message = "主旨長度不可超過200字元")
    @Schema(description = "Email 主旨範本")
    private String subject;

    /**
     * 通知內容範本
     */
    @Size(min = 1, max = 5000, message = "範本內容長度必須在1-5000字元之間")
    @Schema(description = "通知內容範本（支援變數 {{variableName}}）")
    private String body;

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
    @Schema(description = "範本變數定義（key=變數名, value=說明）")
    private Map<String, String> variables;

    /**
     * 是否啟用
     */
    @Schema(description = "是否啟用", example = "true")
    private Boolean isActive;
}
