package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.util.List;

import com.company.hrms.project.domain.model.valueobject.BudgetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 專案成本分析回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProjectCostResponse {

    /**
     * 專案 ID
     */
    private String projectId;

    /**
     * 專案代碼
     */
    private String projectCode;

    /**
     * 專案名稱
     */
    private String projectName;

    /**
     * 預算資訊
     */
    private BudgetInfo budget;

    /**
     * 成本摘要
     */
    private CostSummary summary;

    /**
     * 依成員統計
     */
    private List<MemberCost> byMember;

    /**
     * 依月份統計
     */
    private List<MonthlyCost> byMonth;

    /**
     * 預算資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetInfo {
        private BudgetType budgetType;
        private BigDecimal budgetAmount;
        private BigDecimal budgetHours;
    }

    /**
     * 成本摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostSummary {
        private BigDecimal totalHours;
        private BigDecimal totalCost;
        private BigDecimal budgetUtilization;
        private BigDecimal hoursUtilization;
        private BigDecimal estimatedGrossProfit;
        private BigDecimal estimatedGrossProfitMargin;
        private BigDecimal burnRate;
    }

    /**
     * 成員成本
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberCost {
        private String employeeId;
        private String employeeName;
        private String role;
        private BigDecimal hours;
        private BigDecimal hourlyRate;
        private BigDecimal cost;
        private BigDecimal costPercentage;
    }

    /**
     * 月份成本
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCost {
        private String yearMonth;
        private BigDecimal hours;
        private BigDecimal cost;
    }
}
