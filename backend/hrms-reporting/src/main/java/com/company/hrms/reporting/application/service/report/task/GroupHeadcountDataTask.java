package com.company.hrms.reporting.application.service.report.task;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;

/**
 * 人力盤點數據分組任務
 */
@Component
public class GroupHeadcountDataTask implements PipelineTask<GetHeadcountReportContext> {

    @Override
    public void execute(GetHeadcountReportContext ctx) {
        String dimension = ctx.getRequest().getDimension();
        List<EmployeeRosterReadModel> employees = ctx.getEmployees();

        Map<String, List<EmployeeRosterReadModel>> groupedData;
        if ("POSITION".equalsIgnoreCase(dimension)) {
            groupedData = employees.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getPositionName() != null ? e.getPositionName() : "Unknown"));
        } else {
            // Default to DEPARTMENT
            groupedData = employees.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getDepartmentName() != null ? e.getDepartmentName() : "Unknown"));
        }
        ctx.setGroupedData(groupedData);
    }
}
