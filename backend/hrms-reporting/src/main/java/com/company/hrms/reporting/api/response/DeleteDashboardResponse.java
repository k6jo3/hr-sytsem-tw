package com.company.hrms.reporting.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刪除儀表板回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "刪除儀表板回應")
public class DeleteDashboardResponse {

    @Schema(description = "訊息")
    private String message;
}
