package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.CreateCycleContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;

import lombok.RequiredArgsConstructor;

/**
 * 建立考核週期 Task (Domain)
 */
@Component
@RequiredArgsConstructor
public class CreateCycleTask implements PipelineTask<CreateCycleContext> {

    @Override
    public void execute(CreateCycleContext context) throws Exception {
        // 呼叫 Domain 方法：建立週期
        PerformanceCycle cycle = PerformanceCycle.create(
                context.getCycleName(),
                context.getCycleType(),
                context.getStartDate(),
                context.getEndDate(),
                context.getSelfEvalDeadline(),
                context.getManagerEvalDeadline());

        context.setCycle(cycle);
        context.setCycleId(cycle.getCycleId().getValue().toString());
    }

    @Override
    public String getName() {
        return "建立考核週期";
    }
}
