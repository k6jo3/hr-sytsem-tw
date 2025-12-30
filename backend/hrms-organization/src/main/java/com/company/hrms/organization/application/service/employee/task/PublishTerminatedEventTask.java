package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeeTerminatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布離職事件 Task (Integration Task)
 * 發布領域事件通知其他服務 (IAM, Attendance, Insurance, Payroll, Project)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishTerminatedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        TerminateEmployeeRequest request = context.getTerminateRequest();

        UUID employeeIdUuid = UUID.fromString(context.getEmployeeId());
        String companyEmail = employee.getCompanyEmail() != null
                ? employee.getCompanyEmail().getValue()
                : null;

        EmployeeTerminatedEvent event = new EmployeeTerminatedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                companyEmail,
                employee.getOrganizationId(),
                employee.getDepartmentId(),
                request.getTerminationDate(),
                request.getReason());

        eventPublisher.publishEvent(event);

        log.info("離職事件發布: {} - {}",
                employee.getFullName(),
                request.getTerminationDate());
    }

    @Override
    public String getName() {
        return "發布離職事件";
    }
}
