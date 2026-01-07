package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;

import lombok.RequiredArgsConstructor;

/**
 * 確認最終評等 Task (Domain)
 */
@Component
@RequiredArgsConstructor
public class FinalizeReviewTask implements PipelineTask<FinalizeReviewContext> {

    @Override
    public void execute(FinalizeReviewContext context) throws Exception {
        // 呼叫 Domain 方法：確認最終評等
        context.getReview().finalize(
                context.getFinalScore(),
                context.getFinalRating(),
                context.getAdjustmentReason());
    }

    @Override
    public String getName() {
        return "確認最終評等";
    }
}
