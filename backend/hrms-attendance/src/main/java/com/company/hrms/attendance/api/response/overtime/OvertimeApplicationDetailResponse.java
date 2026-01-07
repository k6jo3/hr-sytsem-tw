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
 * 加班申請詳情 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "加班申請詳情")
public class OvertimeApplicationDetailResponse {

    @Schema(description = "申請ID")
    private String applicationId;

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

    @Schema(description = "加班日期")
    private LocalDate overtimeDate;

    @Schema(description = "開始時間")
    private LocalTime startTime;

    @Schema(description = "結束時間")
    private LocalTime endTime;

    @Schema(description = "申請加班時數")
    private BigDecimal requestedHours;

    @Schema(description = "實際加班時數")
    private BigDecimal actualHours;

    @Schema(description = "加班類型", allowableValues = {"WEEKDAY", "WEEKEND", "HOLIDAY"})
    private String overtimeType;

    @Schema(description = "加班事由")
    private String reason;

    @Schema(description = "專案ID (選填)")
    private String projectId;

    @Schema(description = "專案名稱")
    private String projectName;

    @Schema(description = "申請狀態", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private String status;

    @Schema(description = "審核者ID")
    private String approverId;

    @Schema(description = "審核者姓名")
    private String approverName;

    @Schema(description = "審核意見")
    private String approverComment;

    @Schema(description = "申請時間")
    private LocalDateTime appliedAt;

    @Schema(description = "審核時間")
    private LocalDateTime reviewedAt;

    @Schema(description = "補償方式", allowableValues = {"PAY", "COMPENSATORY_LEAVE", "BOTH"})
    private String compensationType;
}
