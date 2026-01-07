package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.CreateCycleContext;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核週期 Task (Infrastructure) - for CreateCycle
 */
@Component("saveCycleForCreateTask")
@RequiredArgsConstructor
public class SaveCycleForCreateTask implements PipelineTask<CreateCycleContext> {

    private final IPerformanceCycleRepository cycleRepository;

    @Override
    public void execute(CreateCycleContext context) throws Exception {
        cycleRepository.save(context.getCycle());
    }

    @Override
    public String getName() {
        return "儲存考核週期(Create)";
    }
}
