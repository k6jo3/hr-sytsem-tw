package com.company.hrms.recruitment.application.dto.interview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 面試回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "面試回應")
public class InterviewResponse {

    @Schema(description = "面試 ID", example = "int-001")
    private String id;

    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @Schema(description = "應徵者姓名", example = "王小明")
    private String candidateName;

    @Schema(description = "面試輪次", example = "1")
    private int interviewRound;

    @Schema(description = "面試類型", example = "PHONE")
    private InterviewType interviewType;

    @Schema(description = "面試日期時間", example = "2026-01-15T14:00:00")
    private LocalDateTime interviewDate;

    @Schema(description = "面試地點", example = "總部大樓 3F 會議室 A")
    private String location;

    @Schema(description = "面試官 ID 列表")
    private List<UUID> interviewerIds;

    @Schema(description = "面試狀態", example = "SCHEDULED")
    private InterviewStatus status;

    @Schema(description = "面試評估列表")
    private List<EvaluationDto> evaluations;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    /**
     * 面試評估 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "面試評估")
    public static class EvaluationDto {

        @Schema(description = "面試官 ID")
        private UUID interviewerId;

        @Schema(description = "技術分數 (1-5)", example = "4")
        private Integer technicalScore;

        @Schema(description = "溝通分數 (1-5)", example = "4")
        private Integer communicationScore;

        @Schema(description = "文化契合分數 (1-5)", example = "5")
        private Integer cultureFitScore;

        @Schema(description = "整體評等", example = "STRONG_HIRE")
        private String overallRating;

        @Schema(description = "評語")
        private String comments;

        @Schema(description = "優勢")
        private String strengths;

        @Schema(description = "疑慮")
        private String concerns;

        @Schema(description = "評估時間")
        private LocalDateTime evaluatedAt;
    }
}
