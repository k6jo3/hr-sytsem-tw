package com.company.hrms.performance.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢考核詳情請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢考核詳情請求")
public class GetReviewDetailRequest {

    @Schema(description = "考核單ID", example = "REVIEW-001")
    private String reviewId;
}
