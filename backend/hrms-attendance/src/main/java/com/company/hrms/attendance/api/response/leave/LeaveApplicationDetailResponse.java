package com.company.hrms.attendance.api.response.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 請假申請詳情 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "請假申請詳情")
public class LeaveApplicationDetailResponse {

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

    @Schema(description = "假別ID")
    private String leaveTypeId;

    @Schema(description = "假別代碼")
    private String leaveTypeCode;

    @Schema(description = "假別名稱")
    private String leaveTypeName;

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;

    @Schema(description = "開始時間半天類型", allowableValues = {"FULL", "AM", "PM"})
    private String startHalfDay;

    @Schema(description = "結束時間半天類型", allowableValues = {"FULL", "AM", "PM"})
    private String endHalfDay;

    @Schema(description = "請假天數")
    private BigDecimal leaveDays;

    @Schema(description = "請假事由")
    private String reason;

    @Schema(description = "代理人ID")
    private String agentId;

    @Schema(description = "代理人姓名")
    private String agentName;

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

    @Schema(description = "附件列表")
    private java.util.List<String> attachments;
}
