package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;

import lombok.RequiredArgsConstructor;

/**
 * 初始化預借薪資 Task
 * 建立新的 SalaryAdvance 聚合根
 */
@Component
@RequiredArgsConstructor
public class InitSalaryAdvanceTask implements PipelineTask<SalaryAdvanceContext> {

    @Override
    public void execute(SalaryAdvanceContext context) throws Exception {
        SalaryAdvance advance = new SalaryAdvance(
                AdvanceId.generate(),
                context.getEmployeeId(),
                context.getRequestedAmount(),
                context.getInstallmentMonths(),
                context.getReason());

        context.setSalaryAdvance(advance);
    }

    @Override
    public String getName() {
        return "初始化預借薪資申請";
    }
}
