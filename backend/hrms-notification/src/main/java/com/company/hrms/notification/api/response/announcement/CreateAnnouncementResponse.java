package com.company.hrms.notification.api.response.announcement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 發布公告回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "發布公告回應")
public class CreateAnnouncementResponse {

    /**
     * 公告 ID
     */
    @Schema(description = "公告 ID", example = "ann-001")
    private String announcementId;

    /**
     * 公告標題
     */
    @Schema(description = "公告標題", example = "2026年春節放假公告")
    private String title;

    /**
     * 公告狀態
     */
    @Schema(description = "公告狀態", example = "PUBLISHED")
    private String status;

    /**
     * 收件人數量
     */
    @Schema(description = "收件人數量", example = "500")
    private Integer recipientCount;

    /**
     * 發布時間
     */
    @Schema(description = "發布時間", example = "2025-12-30T09:00:00")
    private LocalDateTime publishedAt;

    /**
     * 過期時間
     */
    @Schema(description = "過期時間", example = "2026-02-05T23:59:59")
    private LocalDateTime expireAt;
}
