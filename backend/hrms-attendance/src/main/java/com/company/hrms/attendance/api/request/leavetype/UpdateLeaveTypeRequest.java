package com.company.hrms.attendance.api.request.leavetype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新假別請求 DTO
 */
@Data
@Schema(description = "更新假別請求")
public class UpdateLeaveTypeRequest {

    @Size(min = 1, max = 255, message = "假別名稱長度需在1-255字元之間")
    @Schema(description = "假別名稱", example = "特休假（已更新）")
    private String leaveTypeName;

    @Schema(description = "是否帶薪")
    private Boolean isPaid;

    @Schema(description = "年度額度天數", example = "10")
    private BigDecimal annualQuotaDays;

    @Schema(description = "是否可結轉")
    private Boolean allowCarryOver;

    @Schema(description = "最大結轉天數")
    private BigDecimal maxCarryOverDays;

    @Schema(description = "是否需要證明文件")
    private Boolean requiresProof;

    @Schema(description = "最少申請天數", example = "0.5")
    private BigDecimal minimumDays;

    @Schema(description = "是否計入考勤")
    private Boolean affectsAttendance;

    @Schema(description = "適用性別", allowableValues = {"ALL", "MALE", "FEMALE"})
    private String applicableGender;

    @Schema(description = "說明")
    private String description;
}
