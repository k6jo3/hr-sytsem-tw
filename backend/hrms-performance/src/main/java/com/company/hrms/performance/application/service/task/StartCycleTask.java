package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.StartCycleContext;

import lombok.RequiredArgsConstructor;

/**
 * 啟動考核週期 Task (Domain)
 */
@Component
@RequiredArgsConstructor
public class StartCycleTask implements PipelineTask<StartCycleContext> {

    @Override
    public void execute(StartCycleContext context) throws Exception {
        // 呼叫 Domain 方法：啟動週期
        context.getCycle().start();
    }

    @Override
    public String getName() {
        return "啟動考核週期";
    }
}
