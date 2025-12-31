package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.StartPayrollRunContext;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存薪資批次任務
 */
@Component
@RequiredArgsConstructor
public class SavePayrollRunTask implements PipelineTask<StartPayrollRunContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(StartPayrollRunContext context) {
        if (context.getPayrollRun() != null) {
            repository.save(context.getPayrollRun());
        }
    }

    @Override
    public String getName() {
        return "SavePayrollRunTask";
    }
}
