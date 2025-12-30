package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.PayrollRunActionContext;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入薪資批次任務（用於操作）
 */
@Component
@RequiredArgsConstructor
public class FetchPayrollRunForActionTask implements PipelineTask<PayrollRunActionContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(PayrollRunActionContext context) {
        String runId = context.getRequest().getRunId();
        if (runId == null) {
            throw new IllegalArgumentException("批次 ID 為必填");
        }

        PayrollRun run = repository.findById(new RunId(runId))
                .orElseThrow(() -> new DomainException("PAYROLL_RUN_NOT_FOUND", "找不到薪資批次: " + runId));

        context.setPayrollRun(run);
    }

    @Override
    public String getName() {
        return "FetchPayrollRunForActionTask";
    }
}
