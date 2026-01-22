package com.company.hrms.recruitment.application.dto.interview;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重新排程面試請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "重新排程面試請求")
public class RescheduleInterviewRequest {

    @NotNull(message = "新的面試日期不可為空")
    @Schema(description = "新的面試日期時間", example = "2026-01-20T10:00:00")
    private LocalDateTime newInterviewDate;

    @Schema(description = "新的面試地點", example = "視訊會議室 B")
    private String newLocation;
}
