package com.company.hrms.payroll.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.StartPayrollRunContext;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

import lombok.RequiredArgsConstructor;

/**
 * 初始化薪資批次任務
 */
@Component
@RequiredArgsConstructor
public class InitPayrollRunTask implements PipelineTask<StartPayrollRunContext> {

    @Override
    public void execute(StartPayrollRunContext context) {
        PayrollSystem system = PayrollSystem.valueOf(context.getRequest().getPayrollSystem());
        PayPeriod period = new PayPeriod(context.getRequest().getStartDate(), context.getRequest().getEndDate());

        String userId = context.getCurrentUser() != null ? context.getCurrentUser().getUserId() : "SYSTEM";
        String orgId = context.getRequest().getOrganizationId() != null ? context.getRequest().getOrganizationId()
                : "ORG-001";

        PayrollRun run = PayrollRun.create(
                new RunId(UUID.randomUUID().toString()),
                context.getRequest().getName(),
                orgId,
                period,
                system,
                context.getRequest().getEndDate(),
                userId);

        context.setPayrollRun(run);
    }

    @Override
    public String getName() {
        return "InitPayrollRunTask";
    }
}
