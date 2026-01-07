package com.company.hrms.attendance.api.response.leavetype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 假別列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "假別列表項目")
public class LeaveTypeListResponse {

    @Schema(description = "假別ID")
    private String leaveTypeId;

    @Schema(description = "假別代碼")
    private String leaveTypeCode;

    @Schema(description = "假別名稱")
    private String leaveTypeName;

    @Schema(description = "是否帶薪")
    private Boolean isPaid;

    @Schema(description = "年度額度天數")
    private BigDecimal annualQuotaDays;

    @Schema(description = "是否可結轉")
    private Boolean allowCarryOver;

    @Schema(description = "是否啟用")
    private Boolean isActive;

    @Schema(description = "適用性別")
    private String applicableGender;
}
