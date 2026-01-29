package com.company.hrms.reporting.api.response;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 薪資匯總報表回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "薪資匯總報表回應")
public class PayrollSummaryResponse {

    @Schema(description = "薪資資料列表")
    private List<PayrollSummaryItem> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    @Schema(description = "薪資總計")
    private PayrollTotal total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "薪資匯總項目")
    public static class PayrollSummaryItem {

        @Schema(description = "員工編號")
        private String employeeId;

        @Schema(description = "員工姓名")
        private String employeeName;

        @Schema(description = "部門")
        private String departmentName;

        @Schema(description = "基本薪資")
        private BigDecimal baseSalary;

        @Schema(description = "加班費")
        private BigDecimal overtimePay;

        @Schema(description = "津貼")
        private BigDecimal allowances;

        @Schema(description = "獎金")
        private BigDecimal bonus;

        @Schema(description = "應發薪資")
        private BigDecimal grossPay;

        @Schema(description = "勞保費")
        private BigDecimal laborInsurance;

        @Schema(description = "健保費")
        private BigDecimal healthInsurance;

        @Schema(description = "所得稅")
        private BigDecimal incomeTax;

        @Schema(description = "其他扣款")
        private BigDecimal otherDeductions;

        @Schema(description = "實發薪資")
        private BigDecimal netPay;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "薪資總計")
    public static class PayrollTotal {

        @Schema(description = "總人數")
        private Integer totalEmployees;

        @Schema(description = "應發薪資總計")
        private BigDecimal totalGrossPay;

        @Schema(description = "扣款總計")
        private BigDecimal totalDeductions;

        @Schema(description = "實發薪資總計")
        private BigDecimal totalNetPay;
    }
}
