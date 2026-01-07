package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 確認最終評等請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "確認最終評等請求")
public class FinalizeReviewRequest {

    @Schema(description = "考核記錄ID")
    private String reviewId;

    @Schema(description = "最終評等", example = "A")
    private String finalRating;

    @Schema(description = "調整原因")
    private String adjustmentReason;
}
