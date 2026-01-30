package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryCondition.EQ;

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
    @EQ
    private String employeeId;

    @Schema(description = "部門 ID")
    private String deptId;

    @Schema(description = "日期")
    @EQ
    private LocalDate date;

    @Schema(description = "月份 (YYYY-MM)")
    private String month;

    @Schema(description = "狀態 (NORMAL/ABNORMAL)")
    @EQ
    private String status;

    @Schema(description = "是否遲到")
    @EQ("isLate")
    private Boolean lateFlag;

    @Schema(description = "是否早退")
    @EQ("isEarlyLeave")
    private Boolean earlyLeaveFlag;
}
