package com.company.hrms.attendance.api.request.shift;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢班別列表請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢班別列表請求")
public class GetShiftListRequest {

    @Schema(description = "組織ID")
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    @Schema(description = "班別類型")
    @QueryFilter(operator = Operator.EQ, property = "type")
    private String shiftType;

    @Schema(description = "是否啟用")
    @QueryFilter(operator = Operator.EQ)
    private Boolean isActive;
}
