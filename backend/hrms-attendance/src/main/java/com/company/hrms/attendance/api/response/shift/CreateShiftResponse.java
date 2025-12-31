package com.company.hrms.attendance.api.response.shift;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 建立班別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立班別回應")
public class CreateShiftResponse {

    @Schema(description = "班別ID")
    private String shiftId;

    @Schema(description = "班別代碼")
    private String shiftCode;

    @Schema(description = "班別名稱")
    private String shiftName;

    @Schema(description = "工作時數")
    private BigDecimal workingHours;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
