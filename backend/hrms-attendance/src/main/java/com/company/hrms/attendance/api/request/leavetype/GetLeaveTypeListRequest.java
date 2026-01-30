package com.company.hrms.attendance.api.request.leavetype;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 假別列表查詢請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "假別列表查詢請求")
public class GetLeaveTypeListRequest {

    @Schema(description = "組織ID")
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    @Schema(description = "是否帶薪")
    @QueryFilter(operator = Operator.EQ)
    private Boolean isPaid;

    @Schema(description = "是否啟用")
    @QueryFilter(operator = Operator.EQ)
    private Boolean isActive;
}
