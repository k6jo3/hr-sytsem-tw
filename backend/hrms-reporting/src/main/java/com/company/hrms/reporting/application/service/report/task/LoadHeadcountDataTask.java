package com.company.hrms.reporting.application.service.report.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入人力盤點原始數據任務
 */
@Component
@RequiredArgsConstructor
public class LoadHeadcountDataTask implements PipelineTask<GetHeadcountReportContext> {

    private final EmployeeRosterReadModelRepository repository;

    @Override
    public void execute(GetHeadcountReportContext ctx) {
        List<EmployeeRosterReadModel> employees = repository.findAll().stream()
                .filter(e -> e.getTenantId().equals(ctx.getTenantId()))
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());
        ctx.setEmployees(employees);
    }
}
