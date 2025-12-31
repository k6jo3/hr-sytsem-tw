package com.company.hrms.attendance.api.request.shift;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

/**
 * 更新班別請求 DTO
 */
@Data
@Schema(description = "更新班別請求")
public class UpdateShiftRequest {

    @Size(min = 1, max = 255, message = "班別名稱長度需在1-255字元之間")
    @Schema(description = "班別名稱", example = "標準班（已更新）")
    private String shiftName;

    @Schema(description = "班別類型", allowableValues = {"STANDARD", "FLEXIBLE", "ROTATING"})
    private String shiftType;

    @Schema(description = "上班時間", example = "09:00")
    private LocalTime workStartTime;

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
