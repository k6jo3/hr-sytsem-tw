package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入考核週期 Task (Infrastructure)
 */
@Component
@RequiredArgsConstructor
public class LoadCycleTask implements PipelineTask<StartCycleContext> {

    private final IPerformanceCycleRepository cycleRepository;

    @Override
    public void execute(StartCycleContext context) throws Exception {
        PerformanceCycle cycle = cycleRepository
                .findById(CycleId.of(context.getCycleId()))
                .orElseThrow(() -> new IllegalArgumentException("考核週期不存在: " + context.getCycleId()));

        context.setCycle(cycle);
    }

    @Override
    public String getName() {
        return "載入考核週期";
    }
}
