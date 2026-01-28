package com.company.hrms.notification.api.response.notification;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 標記全部通知為已讀回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "標記全部通知為已讀回應")
public class MarkAllReadResponse {

    /**
     * 已標記的通知數量
     */
    @Schema(description = "已標記的通知數量", example = "5")
    private Integer markedCount;

    /**
     * 已讀時間
     */
    @Schema(description = "已讀時間", example = "2025-12-30T10:30:00")
    private LocalDateTime readAt;
}
