package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 標記通知為已讀回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "標記通知為已讀回應")
public class MarkNotificationReadResponse {

    /**
     * 通知 ID
     */
    @Schema(description = "通知 ID", example = "ntf-001")
    private String notificationId;

    /**
     * 通知狀態
     */
    @Schema(description = "通知狀態", example = "READ")
    private String status;

    /**
     * 已讀時間
     */
    @Schema(description = "已讀時間", example = "2025-12-30T10:30:00")
    private LocalDateTime readAt;
}
