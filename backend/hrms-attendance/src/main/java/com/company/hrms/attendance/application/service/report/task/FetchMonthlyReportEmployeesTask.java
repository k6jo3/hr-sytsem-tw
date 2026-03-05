package com.company.hrms.attendance.application.service.report.task;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeListResponseDto;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 獲取月度報表員工名單 Task
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchMonthlyReportEmployeesTask implements PipelineTask<MonthlyReportContext> {

    private final OrganizationServiceClient organizationClient;

    @Override
    public void execute(MonthlyReportContext context) throws Exception {
        log.info("Fetching employees for monthly report. OrgId: {}, DeptId: {}",
                context.getRequest().getOrganizationId(), context.getRequest().getDepartmentId());

        try {
            EmployeeListResponseDto response = organizationClient.getEmployeeList(
                    Collections.singletonList("ACTIVE"),
                    context.getRequest().getOrganizationId(),
                    context.getRequest().getDepartmentId(),
                    0, 1000);

            context.setEmployees(response.getData());
        } catch (Exception e) {
            log.warn("Organization Service 不可用，使用空員工名單進行降級處理", e);
            context.setEmployees(Collections.emptyList());
        }
    }
}
