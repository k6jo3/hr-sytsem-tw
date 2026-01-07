package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.SubmitReviewContext;

import lombok.RequiredArgsConstructor;

/**
 * 提交考核評估 Task (Domain)
 */
@Component
@RequiredArgsConstructor
public class SubmitEvaluationTask implements PipelineTask<SubmitReviewContext> {

    @Override
    public void execute(SubmitReviewContext context) throws Exception {
        // 呼叫 Domain 方法：提交評估
        context.getReview().submitEvaluation(
                context.getEvaluationItems(),
                context.getComments());
    }

    @Override
    public String getName() {
        return "提交考核評估";
    }
}
