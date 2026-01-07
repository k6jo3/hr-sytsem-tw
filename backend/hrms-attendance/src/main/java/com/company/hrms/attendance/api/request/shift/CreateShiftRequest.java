package com.company.hrms.attendance.api.request.shift;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 建立班別請求 DTO
 */
@Data
@Schema(description = "建立班別請求")
public class CreateShiftRequest {

    @NotBlank(message = "班別代碼不可為空")
    @Size(min = 1, max = 50, message = "班別代碼長度需在1-50字元之間")
    @Schema(description = "班別代碼", example = "STD-01")
    private String shiftCode;

    @NotBlank(message = "班別名稱不可為空")
    @Size(min = 1, max = 255, message = "班別名稱長度需在1-255字元之間")
    @Schema(description = "班別名稱", example = "標準班")
    private String shiftName;

    @NotBlank(message = "組織ID不可為空")
    @Schema(description = "組織ID")
    private String organizationId;

    @NotBlank(message = "班別類型不可為空")
    @Schema(description = "班別類型", allowableValues = { "STANDARD", "FLEXIBLE", "ROTATING" })
    private String shiftType;

    @NotNull(message = "上班時間不可為空")
    @Schema(description = "上班時間", example = "09:00")
    private LocalTime workStartTime;

    @NotNull(message = "下班時間不可為空")
    @Schema(description = "下班時間", example = "18:00")
    private LocalTime workEndTime;

    @Schema(description = "休息開始時間", example = "12:00")
    private LocalTime breakStartTime;

    @Schema(description = "休息結束時間", example = "13:00")
    private LocalTime breakEndTime;

    @Min(value = 0, message = "遲到容許分鐘需大於等於0")
    @Max(value = 60, message = "遲到容許分鐘需小於等於60")
    @Schema(description = "遲到容許分鐘", example = "5")
    private Integer lateToleranceMinutes;

    @Min(value = 0, message = "早退容許分鐘需大於等於0")
    @Max(value = 60, message = "早退容許分鐘需小於等於60")
    @Schema(description = "早退容許分鐘", example = "0")
    private Integer earlyLeaveToleranceMinutes;
}
