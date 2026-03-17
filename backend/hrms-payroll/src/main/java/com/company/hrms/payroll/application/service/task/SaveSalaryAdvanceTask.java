package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存預借薪資 Task
 * 將 SalaryAdvance 聚合根持久化
 */
@Component
@RequiredArgsConstructor
public class SaveSalaryAdvanceTask implements PipelineTask<SalaryAdvanceContext> {

    private final ISalaryAdvanceRepository repository;

    @Override
    public void execute(SalaryAdvanceContext context) throws Exception {
        repository.save(context.getSalaryAdvance());
    }

    @Override
    public String getName() {
        return "儲存預借薪資";
    }
}
