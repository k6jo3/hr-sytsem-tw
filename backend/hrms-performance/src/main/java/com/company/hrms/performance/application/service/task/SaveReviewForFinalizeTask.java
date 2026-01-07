package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核記錄 Task (Infrastructure) - for FinalizeReview
 */
@Component("saveReviewForFinalizeTask")
@RequiredArgsConstructor
public class SaveReviewForFinalizeTask implements PipelineTask<FinalizeReviewContext> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public void execute(FinalizeReviewContext context) throws Exception {
        reviewRepository.save(context.getReview());
    }

    @Override
    public String getName() {
        return "儲存考核記錄(Finalize)";
    }
}
