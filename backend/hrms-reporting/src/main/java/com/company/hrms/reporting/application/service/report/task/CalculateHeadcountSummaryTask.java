package com.company.hrms.reporting.application.service.report.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountSummary;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;

/**
 * 計算人力盤點總計摘要任務
 */
@Component
public class CalculateHeadcountSummaryTask implements PipelineTask<GetHeadcountReportContext> {

    @Override
    public void execute(GetHeadcountReportContext ctx) {
        List<EmployeeRosterReadModel> employees = ctx.getEmployees();

        HeadcountSummary summary = HeadcountSummary.builder()
                .grandTotal(employees.size())
                .totalActive((int) employees.stream()
                        .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count())
                .totalProbation((int) employees.stream()
                        .filter(e -> "PROBATION".equalsIgnoreCase(e.getStatus())).count())
                .totalLeave((int) employees.stream()
                        .filter(e -> "LEAVE".equalsIgnoreCase(e.getStatus())).count())
                .newHires(0)
                .terminations(0)
                .turnoverRate(0.0)
                .build();

        ctx.setSummary(summary);
    }
}
