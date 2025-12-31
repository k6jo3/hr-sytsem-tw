package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 開始執行薪資計算任務
 * 將批次狀態設為計算中
 */
@Component
@RequiredArgsConstructor
public class StartExecutionTask implements PipelineTask<CalculatePayrollContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(CalculatePayrollContext context) {
        String userId = context.getCurrentUser() != null ? context.getCurrentUser().getUserId() : "SYSTEM";
        int totalCount = context.getEligibleStructures().size();

        context.getPayrollRun().startExecution(userId, totalCount);
        repository.save(context.getPayrollRun());
    }

    @Override
    public String getName() {
        return "StartExecutionTask";
    }
}
