package com.company.hrms.attendance.api.request.attendance;

import com.company.hrms.common.query.QueryCondition.EQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得假別餘額列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得假別餘額列表請求")
public class GetLeaveBalanceRequest {

    @Schema(description = "員工 ID")
    @EQ
    private String employeeId;

    @Schema(description = "部門 ID")
    @EQ("department_id")
    private String deptId;

    @Schema(description = "年度 (YYYY)")
    @EQ
    private Integer year;

    @Schema(description = "假別代碼")
    @EQ("leave_type_id")
    private String leaveType;
}
