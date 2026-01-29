package com.company.hrms.reporting.api.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人力盤點報表回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "人力盤點報表回應")
public class HeadcountReportResponse {

    @Schema(description = "人力盤點資料列表")
    private List<HeadcountItem> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    @Schema(description = "統計摘要")
    private HeadcountSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "人力盤點項目")
    public static class HeadcountItem {

        @Schema(description = "維度名稱 (部門/職位/職級)")
        private String dimensionName;

        @Schema(description = "在職人數")
        private Integer activeCount;

        @Schema(description = "試用期人數")
        private Integer probationCount;

        @Schema(description = "留職停薪人數")
        private Integer leaveCount;

        @Schema(description = "總人數")
        private Integer totalCount;

        @Schema(description = "男性人數")
        private Integer maleCount;

        @Schema(description = "女性人數")
        private Integer femaleCount;

        @Schema(description = "平均年資 (年)")
        private Double avgServiceYears;

        @Schema(description = "平均年齡")
        private Double avgAge;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "人力盤點摘要")
    public static class HeadcountSummary {

        @Schema(description = "總在職人數")
        private Integer totalActive;

        @Schema(description = "總試用期人數")
        private Integer totalProbation;

        @Schema(description = "總留職停薪人數")
        private Integer totalLeave;

        @Schema(description = "總人數")
        private Integer grandTotal;

        @Schema(description = "本月新進人數")
        private Integer newHires;

        @Schema(description = "本月離職人數")
        private Integer terminations;

        @Schema(description = "離職率 (%)")
        private Double turnoverRate;
    }
}
