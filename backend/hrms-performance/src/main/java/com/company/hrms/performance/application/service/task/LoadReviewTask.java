package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.SubmitReviewContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入考核記錄 Task (Infrastructure)
 */
@Component
@RequiredArgsConstructor
public class LoadReviewTask implements PipelineTask<SubmitReviewContext> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public void execute(SubmitReviewContext context) throws Exception {
        PerformanceReview review = reviewRepository
                .findById(ReviewId.of(context.getReviewId()))
                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在: " + context.getReviewId()));

        context.setReview(review);
    }

    @Override
    public String getName() {
        return "載入考核記錄";
    }
}
