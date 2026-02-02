package com.company.hrms.attendance.application.service.report.task;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeListResponseDto;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchDailyReportEmployeesTask implements PipelineTask<DailyReportContext> {

    private final OrganizationServiceClient organizationClient;

    @Override
    public void execute(DailyReportContext context) throws Exception {
        log.info("Fetching employees for daily report. OrgId: {}, DeptId: {}",
                context.getRequest().getOrganizationId(), context.getRequest().getDepartmentId());

        try {
            EmployeeListResponseDto response = organizationClient.getEmployeeList(
                    Collections.singletonList("ACTIVE"),
                    context.getRequest().getOrganizationId(),
                    context.getRequest().getDepartmentId(),
                    0, 1000);

            context.setEmployees(response.getData());
        } catch (Exception e) {
            log.error("Failed to fetch employees from Organization Service", e);
            throw new RuntimeException("無法獲取員工名單，請稍後重試");
        }
    }
}
