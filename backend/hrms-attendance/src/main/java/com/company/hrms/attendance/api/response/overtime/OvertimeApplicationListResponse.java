package com.company.hrms.attendance.api.response.overtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 加班申請列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "加班申請列表項目")
public class OvertimeApplicationListResponse {

    @Schema(description = "申請ID")
    private String applicationId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "加班日期")
    private LocalDate overtimeDate;

    @Schema(description = "開始時間")
    private LocalTime startTime;

    @Schema(description = "結束時間")
    private LocalTime endTime;

    @Schema(description = "加班時數")
    private BigDecimal overtimeHours;

    @Schema(description = "加班類型", allowableValues = {"WEEKDAY", "WEEKEND", "HOLIDAY"})
    private String overtimeType;

    @Schema(description = "申請狀態", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private String status;

    @Schema(description = "申請時間")
    private LocalDateTime appliedAt;
}
