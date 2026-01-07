package com.company.hrms.performance.application.service.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;

import lombok.Getter;
import lombok.Setter;

/**
 * 提交考核 Pipeline Context
 */
@Getter
@Setter
public class SubmitReviewContext extends PipelineContext {

    // === 輸入 ===
    /**
     * 考核記錄ID
     */
    private final String reviewId;

    /**
     * 評估項目列表
     */
    private final List<EvaluationItem> evaluationItems;

    /**
     * 評語
     */
    private final String comments;

    // === 中間資料 ===
    /**
     * 載入的考核記錄
     */
    private PerformanceReview review;

    /**
     * 建構子
     */
    public SubmitReviewContext(String reviewId, List<EvaluationItem> evaluationItems, String comments) {
        this.reviewId = reviewId;
        this.evaluationItems = evaluationItems;
        this.comments = comments;
    }
}
