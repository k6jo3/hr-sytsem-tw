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
 * 請假申請列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "請假申請列表項目")
public class LeaveApplicationListResponse {

    @Schema(description = "申請ID")
    private String applicationId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "假別代碼")
    private String leaveTypeCode;

    @Schema(description = "假別名稱")
    private String leaveTypeName;

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;

    @Schema(description = "請假天數")
    private BigDecimal leaveDays;

    @Schema(description = "申請狀態", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private String status;

    @Schema(description = "申請時間")
    private LocalDateTime appliedAt;
}
