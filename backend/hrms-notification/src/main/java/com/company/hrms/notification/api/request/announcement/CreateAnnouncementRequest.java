package com.company.hrms.notification.api.request.announcement;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 發布公告請求
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "發布公告請求")
public class CreateAnnouncementRequest {

    /**
     * 公告標題
     */
    @NotBlank(message = "公告標題不可為空")
    @Size(min = 1, max = 200, message = "公告標題長度必須在 1-200 字元之間")
    @Schema(description = "公告標題", example = "2026年春節放假公告", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * 公告內容
     */
    @NotBlank(message = "公告內容不可為空")
    @Size(max = 5000, message = "公告內容最多 5000 字元")
    @Schema(description = "公告內容", example = "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /**
     * 優先級
     */
    @Schema(description = "優先級", example = "HIGH", defaultValue = "NORMAL")
    private String priority;

    /**
     * 發送渠道
     */
    @Schema(description = "發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> channels;

    /**
     * 目標對象
     */
    @Schema(description = "目標對象")
    private TargetAudience targetAudience;

    /**
     * 發布時間（立即或排程）
     */
    @Schema(description = "發布時間", example = "2025-12-30T09:00:00")
    private LocalDateTime publishAt;

    /**
     * 過期時間
     */
    @Schema(description = "過期時間", example = "2026-02-05T23:59:59")
    private LocalDateTime expireAt;

    /**
     * 目標對象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "目標對象")
    public static class TargetAudience {

        /**
         * 類型：ALL / DEPARTMENT / ROLE
         */
        @Schema(description = "類型", example = "ALL")
        private String type;

        /**
         * 部門 ID 列表
         */
        @Schema(description = "部門 ID 列表")
        private List<String> departmentIds;

        /**
         * 角色 ID 列表
         */
        @Schema(description = "角色 ID 列表")
        private List<String> roleIds;
    }
}
