package com.company.hrms.notification.api.response.notification;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 未讀通知數量回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "未讀通知數量回應")
public class UnreadCountResponse {

    /**
     * 未讀通知數量
     */
    @Schema(description = "未讀通知數量", example = "5")
    private Long unreadCount;

    /**
     * 依類型分組的未讀數量
     */
    @Schema(description = "依類型分組的未讀數量")
    private Map<String, Long> byType;
}
