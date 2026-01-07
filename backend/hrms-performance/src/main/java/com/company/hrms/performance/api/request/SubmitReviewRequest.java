package com.company.hrms.performance.api.request;

import java.util.List;

import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交考核請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提交考核請求")
public class SubmitReviewRequest {

    @Schema(description = "考核記錄ID")
    private String reviewId;

    @Schema(description = "評估項目列表")
    private List<EvaluationItem> evaluationItems;

    @Schema(description = "評語")
    private String comments;
}
