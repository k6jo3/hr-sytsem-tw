package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得出勤列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得出勤列表請求")
public class GetAttendanceListRequest {

    @Schema(description = "員工 ID")
    private String employeeId;

    @Schema(description = "部門 ID")
    private String deptId;

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "月份 (YYYY-MM)")
    private String month;

    @Schema(description = "狀態 (NORMAL/ABNORMAL)")
    private String status;

    @Schema(description = "是否遲到")
    private Boolean lateFlag;

    @Schema(description = "是否早退")
    private Boolean earlyLeaveFlag;
}
