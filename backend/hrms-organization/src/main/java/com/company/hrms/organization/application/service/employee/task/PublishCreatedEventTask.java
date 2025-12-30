package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeeCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布員工建立事件 Task (Integration Task)
 * 發布領域事件通知其他服務 (IAM, Insurance, Payroll)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishCreatedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();

        UUID employeeIdUuid = employee.getId().getValue();
        String companyEmail = employee.getCompanyEmail() != null
                ? employee.getCompanyEmail().getValue()
                : null;

        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                companyEmail,
                employee.getOrganizationId(),
                employee.getDepartmentId(),
                employee.getJobTitle(),
                employee.getHireDate());

        eventPublisher.publishEvent(event);

        log.info("員工建立事件發布: {} - {}",
                employee.getEmployeeNumber(),
                employee.getFullName());
    }

    @Override
    public String getName() {
        return "發布員工建立事件";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getCreateRequest() != null && context.getEmployee() != null;
    }
}
