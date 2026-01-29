package com.company.hrms.reporting.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 專案成本分析回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "專案成本分析回應")
public class ProjectCostAnalysisResponse {

    @Schema(description = "專案成本列表")
    private List<ProjectCostItem> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "專案成本項目")
    public static class ProjectCostItem {

        @Schema(description = "專案ID")
        private String projectId;

        @Schema(description = "專案名稱")
        private String projectName;

        @Schema(description = "客戶名稱")
        private String customerName;

        @Schema(description = "專案經理")
        private String projectManager;

        @Schema(description = "開始日期")
        private LocalDate startDate;

        @Schema(description = "結束日期")
        private LocalDate endDate;

        @Schema(description = "專案狀態")
        private String status;

        @Schema(description = "預算金額")
        private BigDecimal budgetAmount;

        @Schema(description = "人力成本")
        private BigDecimal laborCost;

        @Schema(description = "其他成本")
        private BigDecimal otherCost;

        @Schema(description = "總成本")
        private BigDecimal totalCost;

        @Schema(description = "成本差異")
        private BigDecimal costVariance;

        @Schema(description = "成本差異率 (%)")
        private Double costVarianceRate;

        @Schema(description = "投入工時")
        private Double totalHours;

        @Schema(description = "人力利用率 (%)")
        private Double utilizationRate;
    }
}
