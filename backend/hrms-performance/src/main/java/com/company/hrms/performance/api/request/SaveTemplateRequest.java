package com.company.hrms.performance.api.request;

import java.util.List;

import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 儲存考核表單範本請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "儲存考核表單範本請求")
public class SaveTemplateRequest {

    @Schema(description = "週期ID")
    private String cycleId;

    @Schema(description = "範本名稱", example = "2025年度考核表")
    private String templateName;

    @Schema(description = "評分制度", example = "FIVE_POINT")
    private ScoringSystem scoringSystem;

    @Schema(description = "是否啟用強制分配")
    private Boolean enableDistribution;

    @Schema(description = "評估項目列表")
    private List<EvaluationItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluationItemRequest {
        @Schema(description = "項目名稱", example = "工作品質")
        private String itemName;

        @Schema(description = "權重", example = "30")
        private Integer weight;

        @Schema(description = "說明")
        private String description;

        @Schema(description = "評分標準")
        private String criteria;
    }
}
