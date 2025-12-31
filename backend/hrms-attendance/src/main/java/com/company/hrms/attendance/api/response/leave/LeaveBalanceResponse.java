package com.company.hrms.attendance.api.response.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 假別餘額回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "假別餘額回應")
public class LeaveBalanceResponse {

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "假別餘額明細")
    private List<LeaveBalanceItem> balances;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "假別餘額項目")
    public static class LeaveBalanceItem {

        @Schema(description = "假別ID")
        private String leaveTypeId;

        @Schema(description = "假別代碼")
        private String leaveTypeCode;

        @Schema(description = "假別名稱")
        private String leaveTypeName;

        @Schema(description = "年度額度")
        private BigDecimal annualQuota;

        @Schema(description = "已使用天數")
        private BigDecimal usedDays;

        @Schema(description = "剩餘天數")
        private BigDecimal remainingDays;

        @Schema(description = "待審核天數")
        private BigDecimal pendingDays;

        @Schema(description = "結轉天數")
        private BigDecimal carryOverDays;
    }
}
