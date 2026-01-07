package com.company.hrms.attendance.api.request.attendance;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 補卡申請請求 DTO
 */
@Data
@Schema(description = "補卡申請請求")
public class CreateCorrectionRequest {

    @NotBlank(message = "員工ID不可為空")
    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "出勤記錄ID")
    private String attendanceRecordId;

    @NotNull(message = "補卡日期不可為空")
    @Schema(description = "補卡日期", example = "2025-12-05")
    private LocalDate correctionDate;

    @NotBlank(message = "補卡類型不可為空")
    @Schema(description = "補卡類型", allowableValues = { "FORGET_CHECK_IN", "FORGET_CHECK_OUT", "DEVICE_FAILURE",
            "OUT_FOR_BUSINESS", "OTHER" })
    private String correctionType;

    @Schema(description = "補正上班打卡時間", example = "09:00")
    private LocalTime correctedCheckInTime;

    @Schema(description = "補正下班打卡時間", example = "18:00")
    private LocalTime correctedCheckOutTime;

    @NotBlank(message = "補卡原因不可為空")
    @Size(min = 1, max = 500, message = "補卡原因長度需在1-500字元之間")
    @Schema(description = "補卡原因")
    private String reason;
}
