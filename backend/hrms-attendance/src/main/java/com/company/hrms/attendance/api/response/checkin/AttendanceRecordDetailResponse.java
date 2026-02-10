package com.company.hrms.attendance.api.response.checkin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出勤記錄詳情 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "出勤記錄詳情")
public class AttendanceRecordDetailResponse {

    @Schema(description = "記錄ID")
    private String recordId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "部門名稱")
    private String departmentName;

    @Schema(description = "出勤日期")
    private LocalDate attendanceDate;

    @Schema(description = "班別ID")
    private String shiftId;

    @Schema(description = "班別名稱")
    private String shiftName;

    @Schema(description = "預定上班時間")
    private LocalTime scheduledStartTime;

    @Schema(description = "預定下班時間")
    private LocalTime scheduledEndTime;

    @Schema(description = "上班打卡時間")
    private LocalDateTime checkInTime;

    @Schema(description = "下班打卡時間")
    private LocalDateTime checkOutTime;

    @Schema(description = "上班打卡位置")
    private String checkInLocation;

    @Schema(description = "下班打卡位置")
    private String checkOutLocation;

    @Schema(description = "出勤狀態", allowableValues = { "NORMAL", "LATE", "EARLY_LEAVE", "ABSENT", "LEAVE", "HOLIDAY" })
    private String status;

    @Schema(description = "遲到分鐘數")
    private Integer lateMinutes;

    @Schema(description = "早退分鐘數")
    private Integer earlyLeaveMinutes;

    @Schema(description = "實際工作時數")
    private BigDecimal actualWorkHours;

    @Schema(description = "備註")
    private String remarks;

    @Schema(description = "是否已補卡")
    private Boolean isCorrected;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
