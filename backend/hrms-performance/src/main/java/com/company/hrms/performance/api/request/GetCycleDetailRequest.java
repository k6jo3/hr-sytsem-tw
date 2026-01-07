package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢考核週期詳情請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢考核週期詳情請求")
public class GetCycleDetailRequest {

    @Schema(description = "考核週期 ID", example = "CYCLE-2023001")
    private String cycleId;
}
