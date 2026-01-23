package com.company.hrms.notification.api.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 建立範本回應
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立範本回應")
public class CreateTemplateResponse {

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
     * 是否啟用
     */
    @Schema(description = "是否啟用", example = "true")
    private Boolean isActive;

    /**
     * 建立時間
     */
    @Schema(description = "建立時間", example = "2025-01-23T10:00:00")
    private LocalDateTime createdAt;
}
