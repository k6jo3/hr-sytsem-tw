package com.company.hrms.performance.api.response;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "績效分布回應")
public class GetDistributionResponse {

    @Schema(description = "考核週期ID", example = "CYCLE-2025001")
    private String cycleId;

    @Schema(description = "總考核數", example = "100")
    private int totalReviews;

    @Schema(description = "已完成數", example = "80")
    private int completedReviews;

    @Schema(description = "分布數據 Key:評等 Value:數據")
    private Map<String, DistributionData> distribution;

    @Schema(description = "總數")
    private int totalCount;

    @Schema(description = "總員工數", example = "50")
    private int totalEmployees;

    @Schema(description = "平均分數", example = "3.75")
    private double averageScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionData {
        @Schema(description = "評等", example = "A")
        private String rating;

        @Schema(description = "數量", example = "10")
        private int count;

        @Schema(description = "百分比", example = "10.0")
        private double percentage;
    }
}
