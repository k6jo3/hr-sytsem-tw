package com.company.hrms.performance.application.service.context;

import java.math.BigDecimal;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;

import lombok.Getter;
import lombok.Setter;

/**
 * 確認最終評等 Pipeline Context
 */
@Getter
@Setter
public class FinalizeReviewContext extends PipelineContext {

    // === 輸入 ===
    /**
     * 考核記錄ID
     */
    private final String reviewId;

    /**
     * 最終分數
     */
    private final BigDecimal finalScore;

    /**
     * 最終評等
     */
    private final String finalRating;

    /**
     * 調整原因
     */
    private final String adjustmentReason;

    // === 中間資料 ===
    /**
     * 載入的考核記錄
     */
    private PerformanceReview review;

    /**
     * 建構子
     */
    public FinalizeReviewContext(String reviewId, BigDecimal finalScore,
            String finalRating, String adjustmentReason) {
        this.reviewId = reviewId;
        this.finalScore = finalScore;
        this.finalRating = finalRating;
        this.adjustmentReason = adjustmentReason;
    }
}
