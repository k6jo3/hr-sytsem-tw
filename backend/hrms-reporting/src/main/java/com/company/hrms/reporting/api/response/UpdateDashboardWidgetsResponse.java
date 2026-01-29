package com.company.hrms.reporting.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新 Widget 配置回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新 Widget 配置回應")
public class UpdateDashboardWidgetsResponse {

    @Schema(description = "訊息")
    private String message;

    @Schema(description = "更新的 Widget 數量")
    private Integer updatedCount;
}
