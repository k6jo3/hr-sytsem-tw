package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.StartCycleContext;

import lombok.RequiredArgsConstructor;

/**
 * 完成考核週期 Task (Domain)
 */
@Component
@RequiredArgsConstructor
public class CompleteCycleTask implements PipelineTask<StartCycleContext> {

    @Override
    public void execute(StartCycleContext context) throws Exception {
        // 呼叫 Domain 方法：完成週期
        context.getCycle().complete();
    }

    @Override
    public String getName() {
        return "完成考核週期";
    }
}
