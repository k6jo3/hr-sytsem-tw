package com.company.hrms.training.application.service.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.api.response.ApiResponse;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;
import com.company.hrms.training.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.training.infrastructure.client.organization.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工資訊 Task
 * 負責從組織服務取得員工和主管資訊
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchEmployeeInfoTask implements PipelineTask<EnrollCourseContext> {

    private final OrganizationServiceClient organizationService;

    @Override
    public void execute(EnrollCourseContext context) {
        log.info("Fetching employee info for employeeId: {}", context.getEmployeeId());

        try {
            ApiResponse<EmployeeDto> response = organizationService.getEmployeeDetail(context.getEmployeeId());

            if (response != null && response.isSuccess() && response.getData() != null) {
                EmployeeDto employee = response.getData();
                context.setEmployeeName(employee.getFullName());

                if (employee.getManager() != null) {
                    context.setManagerId(employee.getManager().getEmployeeId());
                    context.setManagerName(employee.getManager().getFullName());
                } else {
                    log.warn("No manager found for employeeId: {}", context.getEmployeeId());
                }
            } else {
                log.error("Failed to fetch employee info from organization service: {}",
                        response != null ? response.getMessage() : "Null response");
                // Fallback or throw exception depending on business requirements
                // Currently using default names to avoid blocking the pipeline if possible
                context.setEmployeeName("Employee " + context.getEmployeeId());
            }
        } catch (Exception e) {
            log.error("Error calling organization service for employeeId: {}", context.getEmployeeId(), e);
            context.setEmployeeName("Employee " + context.getEmployeeId());
        }
    }
}
