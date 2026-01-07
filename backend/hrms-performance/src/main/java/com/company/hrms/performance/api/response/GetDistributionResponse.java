package com.company.hrms.performance.api.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 績效分布回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDistributionResponse {
    private Map<String, DistributionData> distribution;
    private int totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionData {
        private String rating;
        private int count;
        private double percentage;
    }
}
