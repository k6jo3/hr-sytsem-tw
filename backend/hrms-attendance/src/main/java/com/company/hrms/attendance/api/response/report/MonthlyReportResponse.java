package com.company.hrms.attendance.api.response.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月報表回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "月報表回應")
public class MonthlyReportResponse {

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "月份")
    private Integer month;

    @Schema(description = "報表明細列表")
    private List<MonthlyReportItem> items;

    @Schema(description = "總計")
    private ReportSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "月報表明細項目")
    public static class MonthlyReportItem {

        @Schema(description = "員工ID")
        private String employeeId;

        @Schema(description = "員工姓名")
        private String employeeName;

        @Schema(description = "員工編號")
        private String employeeNumber;

        @Schema(description = "部門名稱")
        private String departmentName;

        @Schema(description = "應出勤天數")
        private BigDecimal scheduledDays;

        @Schema(description = "實際出勤天數")
        private BigDecimal actualDays;

        @Schema(description = "缺勤天數")
        private BigDecimal absentDays;

        @Schema(description = "遲到次數")
        private Integer lateCount;

        @Schema(description = "早退次數")
        private Integer earlyLeaveCount;

        @Schema(description = "請假天數")
        private BigDecimal leaveDays;

        @Schema(description = "加班時數")
        private BigDecimal overtimeHours;

        @Schema(description = "總工作時數")
        private BigDecimal totalWorkHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "報表總計")
    public static class ReportSummary {

        @Schema(description = "總員工數")
        private Integer totalEmployees;

        @Schema(description = "平均出勤率")
        private BigDecimal averageAttendanceRate;

        @Schema(description = "總遲到次數")
        private Integer totalLateCount;

        @Schema(description = "總早退次數")
        private Integer totalEarlyLeaveCount;

        @Schema(description = "總加班時數")
        private BigDecimal totalOvertimeHours;
    }
}
