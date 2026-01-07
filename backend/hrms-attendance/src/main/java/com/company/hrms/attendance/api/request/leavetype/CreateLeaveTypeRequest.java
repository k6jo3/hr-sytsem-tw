package com.company.hrms.attendance.api.request.leavetype;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 建立假別請求 DTO
 */
@Data
@Schema(description = "建立假別請求")
public class CreateLeaveTypeRequest {

    @NotBlank(message = "假別代碼不可為空")
    @Size(min = 1, max = 50, message = "假別代碼長度需在1-50字元之間")
    @Schema(description = "假別代碼", example = "ANNUAL")
    private String leaveTypeCode;

    @NotBlank(message = "假別名稱不可為空")
    @Size(min = 1, max = 255, message = "假別名稱長度需在1-255字元之間")
    @Schema(description = "假別名稱", example = "特休假")
    private String leaveTypeName;

    @NotBlank(message = "組織ID不可為空")
    @Schema(description = "組織ID")
    private String organizationId;

    @NotNull(message = "是否帶薪不可為空")
    @Schema(description = "是否帶薪")
    private Boolean isPaid;

    @Schema(description = "年度額度天數", example = "7")
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

    @Schema(description = "適用性別", allowableValues = { "ALL", "MALE", "FEMALE" })
    private String applicableGender;

    @Schema(description = "說明")
    private String description;
}
