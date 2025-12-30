package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeePromotedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布升遷事件 Task (Integration Task)
 * 發布領域事件通知其他服務 (Payroll, Performance)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishPromotedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        PromoteEmployeeRequest request = context.getPromoteRequest();

        UUID employeeIdUuid = UUID.fromString(context.getEmployeeId());
        String oldJobTitle = context.getAttribute("oldJobTitle");
        String oldJobLevel = context.getAttribute("oldJobLevel");

        EmployeePromotedEvent event = new EmployeePromotedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldJobTitle != null ? oldJobTitle : "",
                request.getNewJobTitle() != null ? request.getNewJobTitle() : "",
                oldJobLevel != null ? oldJobLevel : "",
                request.getNewJobLevel() != null ? request.getNewJobLevel() : "",
                request.getEffectiveDate(),
                request.getReason());

        eventPublisher.publishEvent(event);

        log.info("升遷事件發布: {} - {} -> {}",
                employee.getFullName(),
                oldJobTitle,
                request.getNewJobTitle());
    }

    @Override
    public String getName() {
        return "發布升遷事件";
    }
}
