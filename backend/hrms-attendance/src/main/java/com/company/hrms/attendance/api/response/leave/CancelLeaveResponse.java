package com.company.hrms.attendance.api.response.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 取消請假回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取消請假回應")
public class CancelLeaveResponse {

    @Schema(description = "申請ID")
    private String applicationId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "取消時間")
    private LocalDateTime cancelledAt;

    @Schema(description = "是否已退回餘額")
    private Boolean balanceRefunded;

    @Schema(description = "退回天數")
    private BigDecimal refundedDays;
}
