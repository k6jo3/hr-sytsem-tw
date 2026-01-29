package com.company.hrms.reporting.api.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 差勤統計報表回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "差勤統計報表回應")
public class AttendanceStatisticsResponse {

    @Schema(description = "統計資料列表")
    private List<AttendanceStatItem> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "差勤統計項目")
    public static class AttendanceStatItem {

        @Schema(description = "員工編號")
        private String employeeId;

        @Schema(description = "員工姓名")
        private String employeeName;

        @Schema(description = "部門")
        private String departmentName;

        @Schema(description = "應出勤天數")
        private Integer expectedDays;

        @Schema(description = "實際出勤天數")
        private Integer actualDays;

        @Schema(description = "遲到次數")
        private Integer lateCount;

        @Schema(description = "早退次數")
        private Integer earlyLeaveCount;

        @Schema(description = "曠職次數")
        private Integer absentCount;

        @Schema(description = "請假天數")
        private Double leaveDays;

        @Schema(description = "加班時數")
        private Double overtimeHours;

        @Schema(description = "出勤率 (%)")
        private Double attendanceRate;
    }
}
