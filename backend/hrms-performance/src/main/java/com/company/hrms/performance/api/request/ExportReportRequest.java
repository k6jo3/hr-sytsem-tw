package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 匯出績效報表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "匯出績效報表請求")
public class ExportReportRequest {

    @Schema(description = "考核週期ID", example = "CYCLE-2025001")
    private String cycleId;
}
