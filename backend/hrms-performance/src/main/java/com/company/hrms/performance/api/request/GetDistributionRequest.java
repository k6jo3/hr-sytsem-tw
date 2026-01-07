package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢績效分布請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢績效分布請求")
public class GetDistributionRequest {

    @Schema(description = "考核週期ID", example = "CYCLE-2025001")
    private String cycleId;
}
