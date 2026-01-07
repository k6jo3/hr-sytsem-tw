package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.SubmitReviewContext;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核記錄 Task (Infrastructure)
 */
@Component
@RequiredArgsConstructor
public class SaveReviewTask implements PipelineTask<SubmitReviewContext> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public void execute(SubmitReviewContext context) throws Exception {
        reviewRepository.save(context.getReview());
    }

    @Override
    public String getName() {
        return "儲存考核記錄";
    }
}
