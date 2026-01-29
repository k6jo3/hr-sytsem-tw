package com.company.hrms.notification.api.response.announcement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新公告回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新公告回應")
public class UpdateAnnouncementResponse {

    /**
     * 公告 ID
     */
    @Schema(description = "公告 ID", example = "ann-001")
    private String announcementId;

    /**
     * 公告標題
     */
    @Schema(description = "公告標題", example = "2026年春節放假公告（更新版）")
    private String title;

    /**
     * 更新時間
     */
    @Schema(description = "更新時間", example = "2025-12-30T14:00:00")
    private LocalDateTime updatedAt;
}
