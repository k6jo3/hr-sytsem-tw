package com.company.hrms.attendance.api.response.checkin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出勤記錄列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "出勤記錄列表項目")
public class AttendanceRecordListResponse {

    @Schema(description = "記錄ID")
    private String recordId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "出勤日期")
    private LocalDate attendanceDate;

    @Schema(description = "上班打卡時間")
    private LocalDateTime checkInTime;

    @Schema(description = "下班打卡時間")
    private LocalDateTime checkOutTime;

    @Schema(description = "出勤狀態", allowableValues = {"NORMAL", "LATE", "EARLY_LEAVE", "ABSENT", "LEAVE", "HOLIDAY"})
    private String status;

    @Schema(description = "班別名稱")
    private String shiftName;

    @Schema(description = "遲到分鐘數")
    private Integer lateMinutes;

    @Schema(description = "早退分鐘數")
    private Integer earlyLeaveMinutes;
}
