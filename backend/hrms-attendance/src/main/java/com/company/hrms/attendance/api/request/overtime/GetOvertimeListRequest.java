package com.company.hrms.attendance.api.request.overtime;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得加班列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得加班列表請求")
public class GetOvertimeListRequest {

    @Schema(description = "員工 ID")
    private String employeeId;

    @Schema(description = "部門 ID")
    private String deptId;

    @Schema(description = "加班類型 (WORKDAY/HOLIDAY)")
    private String overtimeType;

    @Schema(description = "狀態 (PENDING/APPROVED/REJECTED)")
    private String status;

    @Schema(description = "日期起")
    private LocalDate startDate;

    @Schema(description = "日期迄")
    private LocalDate endDate;
}
