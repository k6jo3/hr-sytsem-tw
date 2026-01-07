package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 啟動考核週期請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "啟動考核週期請求")
public class StartCycleRequest {

    @Schema(description = "週期ID")
    private String cycleId;
}
