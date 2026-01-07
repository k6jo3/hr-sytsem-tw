package com.company.hrms.performance.api.request;

import java.time.LocalDate;

import com.company.hrms.performance.domain.model.valueobject.CycleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新考核週期請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新考核週期請求")
public class UpdateCycleRequest {

    @Schema(description = "週期ID")
    private String cycleId;

    @Schema(description = "週期名稱", example = "2025年度考核")
    private String cycleName;

    @Schema(description = "考核類型", example = "ANNUAL")
    private CycleType cycleType;

    @Schema(description = "考核期間開始日")
    private LocalDate startDate;

    @Schema(description = "考核期間結束日")
    private LocalDate endDate;

    @Schema(description = "自評截止日")
    private LocalDate selfEvalDeadline;

    @Schema(description = "主管評截止日")
    private LocalDate managerEvalDeadline;
}
