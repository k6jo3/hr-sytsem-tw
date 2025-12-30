package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入薪資批次任務
 * 從資料庫讀取指定的 PayrollRun
 */
@Component
@RequiredArgsConstructor
public class FetchPayrollRunTask implements PipelineTask<CalculatePayrollContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(CalculatePayrollContext context) {
        String runIdStr = context.getRequest().getRunId();
        if (runIdStr == null) {
            throw new IllegalArgumentException("批次 ID 為必填");
        }

        PayrollRun run = repository.findById(new RunId(runIdStr))
                .orElseThrow(() -> new DomainException("PAYROLL_RUN_NOT_FOUND", "找不到薪資批次: " + runIdStr));

        context.setPayrollRun(run);
    }

    @Override
    public String getName() {
        return "FetchPayrollRunTask";
    }
}
