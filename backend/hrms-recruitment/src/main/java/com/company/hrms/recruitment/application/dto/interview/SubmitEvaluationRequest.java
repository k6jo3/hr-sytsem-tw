package com.company.hrms.recruitment.application.dto.interview;

import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.OverallRating;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交面試評估請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提交面試評估請求")
public class SubmitEvaluationRequest {

    @NotNull(message = "面試官 ID 不可為空")
    @Schema(description = "面試官 ID")
    private UUID interviewerId;

    @Min(value = 1, message = "技術分數最低為 1")
    @Max(value = 5, message = "技術分數最高為 5")
    @Schema(description = "技術分數 (1-5)", example = "4")
    private Integer technicalScore;

    @Min(value = 1, message = "溝通分數最低為 1")
    @Max(value = 5, message = "溝通分數最高為 5")
    @Schema(description = "溝通分數 (1-5)", example = "4")
    private Integer communicationScore;

    @Min(value = 1, message = "文化契合分數最低為 1")
    @Max(value = 5, message = "文化契合分數最高為 5")
    @Schema(description = "文化契合分數 (1-5)", example = "5")
    private Integer cultureFitScore;

    @NotNull(message = "整體評等不可為空")
    @Schema(description = "整體評等", example = "STRONG_HIRE")
    private OverallRating overallRating;

    @Schema(description = "評語")
    private String comments;

    @Schema(description = "優勢")
    private String strengths;

    @Schema(description = "疑慮")
    private String concerns;
}
