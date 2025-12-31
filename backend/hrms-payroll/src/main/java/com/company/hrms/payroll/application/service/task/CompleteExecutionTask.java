package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 完成薪資計算任務
 * 彙總統計資料並將批次狀態設為完成
 */
@Component
@RequiredArgsConstructor
public class CompleteExecutionTask implements PipelineTask<CalculatePayrollContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(CalculatePayrollContext context) {
        // 建立統計資料
        PayrollStatistics stats = PayrollStatistics.builder()
                .totalEmployees(context.getEligibleStructures().size())
                .processedEmployees(context.getSuccessCount())
                .failedEmployees(context.getFailCount())
                .totalGrossAmount(context.getTotalGross())
                .totalNetAmount(context.getTotalNet())
                .totalDeductions(context.getTotalDeductions())
                .totalOvertimePay(context.getTotalOvertime())
                .build();

        // 完成批次
        context.getPayrollRun().complete(stats);
        repository.save(context.getPayrollRun());
    }

    @Override
    public String getName() {
        return "CompleteExecutionTask";
    }
}
