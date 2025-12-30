package com.company.hrms.payroll.application.service.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.BankTransferContext;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入薪資批次及其薪資單任務
 */
@Component
@RequiredArgsConstructor
public class FetchPayrollRunWithPayslipsTask implements PipelineTask<BankTransferContext> {

    private final IPayrollRunRepository payrollRunRepository;
    private final IPayslipRepository payslipRepository;

    @Override
    public void execute(BankTransferContext context) {
        String runId = context.getRequest().getRunId();
        if (runId == null) {
            throw new IllegalArgumentException("批次 ID 為必填");
        }

        PayrollRun run = payrollRunRepository.findById(new RunId(runId))
                .orElseThrow(() -> new DomainException("PAYROLL_RUN_NOT_FOUND", "找不到薪資批次: " + runId));

        List<Payslip> payslips = payslipRepository.findByPayrollRun(new RunId(runId));

        context.setPayrollRun(run);
        context.setPayslips(payslips);
    }

    @Override
    public String getName() {
        return "FetchPayrollRunWithPayslipsTask";
    }
}
