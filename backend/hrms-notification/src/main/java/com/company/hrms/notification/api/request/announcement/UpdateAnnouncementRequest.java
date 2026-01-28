package com.company.hrms.notification.api.request.announcement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新公告請求
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新公告請求")
public class UpdateAnnouncementRequest {

    /**
     * 公告標題
     */
    @Size(min = 1, max = 200, message = "公告標題長度必須在 1-200 字元之間")
    @Schema(description = "公告標題", example = "2026年春節放假公告（更新版）")
    private String title;

    /**
     * 公告內容
     */
    @Size(max = 5000, message = "公告內容最多 5000 字元")
    @Schema(description = "公告內容", example = "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。")
    private String content;

    /**
     * 優先級
     */
    @Schema(description = "優先級", example = "HIGH")
    private String priority;

    /**
     * 過期時間
     */
    @Schema(description = "過期時間", example = "2026-02-05T23:59:59")
    private LocalDateTime expireAt;
}
