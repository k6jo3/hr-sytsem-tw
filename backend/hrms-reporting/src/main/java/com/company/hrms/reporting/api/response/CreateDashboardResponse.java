package com.company.hrms.reporting.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立儀表板回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立儀表板回應")
public class CreateDashboardResponse {

    @Schema(description = "儀表板 ID")
    private UUID dashboardId;

    @Schema(description = "儀表板名稱")
    private String dashboardName;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "訊息")
    private String message;
}
