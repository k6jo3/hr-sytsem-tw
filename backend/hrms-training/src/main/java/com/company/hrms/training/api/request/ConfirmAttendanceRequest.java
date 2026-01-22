package com.company.hrms.training.api.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "確認出席請求")
public class ConfirmAttendanceRequest {

    @Schema(description = "是否出席")
    @NotNull(message = "出席狀態不能為空")
    private Boolean attended;

    @Schema(description = "實際出席時數")
    private BigDecimal attendedHours;

    @Schema(description = "備註")
    private String remarks;
}
