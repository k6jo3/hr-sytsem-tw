package com.company.hrms.recruitment.application.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 儀表板回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "招募儀表板回應")
public class DashboardResponse {

    @Schema(description = "查詢期間")
    private Period period;

    @Schema(description = "關鍵指標")
    private KPIs kpis;

    @Schema(description = "來源分析")
    private List<SourceAnalytics> sourceAnalytics;

    @Schema(description = "轉換漏斗")
    private ConversionFunnel conversionFunnel;

    @Schema(description = "部門職缺統計")
    private List<DepartmentStats> openingsByDepartment;

    @Schema(description = "月度趨勢")
    private List<MonthlyTrend> monthlyTrend;

    // === 內部類別 ===

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "查詢期間")
    public static class Period {
        @Schema(description = "開始日期", example = "2025-12-01")
        private LocalDate from;

        @Schema(description = "結束日期", example = "2025-12-31")
        private LocalDate to;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "關鍵指標")
    public static class KPIs {
        @Schema(description = "開放職缺數", example = "12")
        private int openJobsCount;

        @Schema(description = "總應徵數", example = "85")
        private int totalApplications;

        @Schema(description = "已安排面試數", example = "23")
        private int interviewsScheduled;

        @Schema(description = "已發 Offer 數", example = "8")
        private int offersExtended;

        @Schema(description = "已錄取人數", example = "5")
        private int hiredCount;

        @Schema(description = "平均到職天數", example = "28")
        private int avgTimeToHire;

        @Schema(description = "Offer 接受率 (%)", example = "62.5")
        private BigDecimal offerAcceptanceRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "來源分析")
    public static class SourceAnalytics {
        @Schema(description = "來源代碼", example = "JOB_BANK")
        private String source;

        @Schema(description = "來源名稱", example = "人力銀行")
        private String sourceLabel;

        @Schema(description = "數量", example = "38")
        private int count;

        @Schema(description = "百分比 (%)", example = "44.7")
        private BigDecimal percentage;

        @Schema(description = "已錄取數", example = "2")
        private int hiredCount;

        @Schema(description = "轉換率 (%)", example = "5.3")
        private BigDecimal conversionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "轉換漏斗")
    public static class ConversionFunnel {
        @Schema(description = "應徵數", example = "85")
        private int applied;

        @Schema(description = "篩選數", example = "45")
        private int screened;

        @Schema(description = "面試數", example = "23")
        private int interviewed;

        @Schema(description = "發 Offer 數", example = "8")
        private int offered;

        @Schema(description = "錄取數", example = "5")
        private int hired;

        @Schema(description = "各階段轉換率")
        private ConversionRates rates;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "轉換率")
    public static class ConversionRates {
        @Schema(description = "篩選率 (%)", example = "52.9")
        private BigDecimal screeningRate;

        @Schema(description = "面試率 (%)", example = "51.1")
        private BigDecimal interviewRate;

        @Schema(description = "Offer 率 (%)", example = "34.8")
        private BigDecimal offerRate;

        @Schema(description = "接受率 (%)", example = "62.5")
        private BigDecimal acceptRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "部門統計")
    public static class DepartmentStats {
        @Schema(description = "部門 ID", example = "dept-001")
        private String departmentId;

        @Schema(description = "部門名稱", example = "研發部")
        private String departmentName;

        @Schema(description = "開放職缺數", example = "5")
        private int openJobs;

        @Schema(description = "應徵者數", example = "35")
        private int candidates;

        @Schema(description = "已錄取數", example = "2")
        private int hired;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "月度趨勢")
    public static class MonthlyTrend {
        @Schema(description = "月份", example = "2025-12")
        private String month;

        @Schema(description = "應徵數", example = "85")
        private int applications;

        @Schema(description = "錄取數", example = "5")
        private int hired;
    }
}
