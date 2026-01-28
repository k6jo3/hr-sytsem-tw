package com.company.hrms.notification.api.response.announcement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤銷公告回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "撤銷公告回應")
public class WithdrawAnnouncementResponse {

    /**
     * 公告 ID
     */
    @Schema(description = "公告 ID", example = "ann-001")
    private String announcementId;

    /**
     * 公告狀態
     */
    @Schema(description = "公告狀態", example = "WITHDRAWN")
    private String status;

    /**
     * 撤銷時間
     */
    @Schema(description = "撤銷時間", example = "2025-12-30T15:00:00")
    private LocalDateTime withdrawnAt;
}
