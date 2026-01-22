package com.company.hrms.recruitment.application.dto.interview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安排面試請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "安排面試請求")
public class ScheduleInterviewRequest {

    @NotBlank(message = "應徵者 ID 不可為空")
    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @Positive(message = "面試輪次必須大於 0")
    @Schema(description = "面試輪次", example = "1")
    private int interviewRound;

    @NotNull(message = "面試類型不可為空")
    @Schema(description = "面試類型", example = "PHONE")
    private InterviewType interviewType;

    @NotNull(message = "面試日期不可為空")
    @Schema(description = "面試日期時間", example = "2026-01-15T14:00:00")
    private LocalDateTime interviewDate;

    @Schema(description = "面試地點", example = "總部大樓 3F 會議室 A")
    private String location;

    @NotNull(message = "面試官列表不可為空")
    @Schema(description = "面試官 ID 列表")
    private List<UUID> interviewerIds;
}
