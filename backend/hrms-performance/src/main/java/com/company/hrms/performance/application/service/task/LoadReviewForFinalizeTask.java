package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入考核記錄 Task (Infrastructure) - for FinalizeReview
 */
@Component("loadReviewForFinalizeTask")
@RequiredArgsConstructor
public class LoadReviewForFinalizeTask implements PipelineTask<FinalizeReviewContext> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public void execute(FinalizeReviewContext context) throws Exception {
        PerformanceReview review = reviewRepository
                .findById(ReviewId.of(context.getReviewId()))
                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在: " + context.getReviewId()));

        context.setReview(review);
    }

    @Override
    public String getName() {
        return "載入考核記錄(Finalize)";
    }
}
