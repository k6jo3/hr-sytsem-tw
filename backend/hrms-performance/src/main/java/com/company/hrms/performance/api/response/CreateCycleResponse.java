package com.company.hrms.performance.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立考核週期回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCycleResponse {
    /**
     * 週期 ID
     */
    private String cycleId;
}
