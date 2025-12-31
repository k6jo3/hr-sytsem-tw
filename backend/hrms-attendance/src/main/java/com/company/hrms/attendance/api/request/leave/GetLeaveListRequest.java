package com.company.hrms.attendance.api.request.leave;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得請假列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得請假列表請求")
public class GetLeaveListRequest {

    @Schema(description = "員工 ID")
    private String employeeId;

    @Schema(description = "部門 ID")
    private String deptId;

    @Schema(description = "假別代碼 (ANNUAL/SICK/etc)")
    private String leaveType;

    @Schema(description = "狀態 (PENDING/APPROVED/REJECTED)")
    private String status;

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;
}
