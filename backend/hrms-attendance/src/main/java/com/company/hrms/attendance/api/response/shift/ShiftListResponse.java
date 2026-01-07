package com.company.hrms.attendance.api.response.shift;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 班別列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "班別列表項目")
public class ShiftListResponse {

    @Schema(description = "班別ID")
    private String shiftId;

    @Schema(description = "班別代碼")
    private String shiftCode;

    @Schema(description = "班別名稱")
    private String shiftName;

    @Schema(description = "班別類型", allowableValues = {"STANDARD", "FLEXIBLE", "ROTATING"})
    private String shiftType;

    @Schema(description = "上班時間")
    private LocalTime workStartTime;

    @Schema(description = "下班時間")
    private LocalTime workEndTime;

    @Schema(description = "工作時數")
    private BigDecimal workingHours;

    @Schema(description = "是否啟用")
    private Boolean isActive;

    @Schema(description = "使用此班別的員工數")
    private Integer employeeCount;
}
