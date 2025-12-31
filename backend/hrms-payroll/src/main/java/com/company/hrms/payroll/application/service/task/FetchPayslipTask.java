package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.PayslipActionContext;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入薪資單任務
 */
@Component
@RequiredArgsConstructor
public class FetchPayslipTask implements PipelineTask<PayslipActionContext> {

    private final IPayslipRepository repository;

    @Override
    public void execute(PayslipActionContext context) {
        if (context.getPayslipId() == null) {
            throw new IllegalArgumentException("薪資單 ID 為必填");
        }

        Payslip payslip = repository.findById(new PayslipId(context.getPayslipId()))
                .orElseThrow(() -> new DomainException("PAYSLIP_NOT_FOUND",
                        "找不到薪資單: " + context.getPayslipId()));

        context.setPayslip(payslip);
    }

    @Override
    public String getName() {
        return "FetchPayslipTask";
    }
}
