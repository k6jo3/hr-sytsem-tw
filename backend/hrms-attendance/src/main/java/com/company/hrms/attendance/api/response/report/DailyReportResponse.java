package com.company.hrms.attendance.api.response.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 日報表回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日報表回應")
public class DailyReportResponse {

    @Schema(description = "報表日期")
    private LocalDate reportDate;

    @Schema(description = "報表明細列表")
    private List<DailyReportItem> items;

    @Schema(description = "總計")
    private DailySummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "日報表明細項目")
    public static class DailyReportItem {

        @Schema(description = "員工ID")
        private String employeeId;

        @Schema(description = "員工姓名")
        private String employeeName;

        @Schema(description = "員工編號")
        private String employeeNumber;

        @Schema(description = "部門名稱")
        private String departmentName;

        @Schema(description = "班別名稱")
        private String shiftName;

        @Schema(description = "上班打卡時間")
        private LocalTime checkInTime;

        @Schema(description = "下班打卡時間")
        private LocalTime checkOutTime;

        @Schema(description = "出勤狀態", allowableValues = {"NORMAL", "LATE", "EARLY_LEAVE", "ABSENT", "LEAVE", "HOLIDAY"})
        private String attendanceStatus;

        @Schema(description = "遲到分鐘數")
        private Integer lateMinutes;

        @Schema(description = "早退分鐘數")
        private Integer earlyLeaveMinutes;

        @Schema(description = "工作時數")
        private BigDecimal workHours;

        @Schema(description = "加班時數")
        private BigDecimal overtimeHours;

        @Schema(description = "備註")
        private String remarks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "日報表總計")
    public static class DailySummary {

        @Schema(description = "應出勤人數")
        private Integer scheduledCount;

        @Schema(description = "實際出勤人數")
        private Integer actualCount;

        @Schema(description = "遲到人數")
        private Integer lateCount;

        @Schema(description = "早退人數")
        private Integer earlyLeaveCount;

        @Schema(description = "缺勤人數")
        private Integer absentCount;

        @Schema(description = "請假人數")
        private Integer leaveCount;

        @Schema(description = "出勤率")
        private BigDecimal attendanceRate;
    }
}
