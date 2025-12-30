package com.company.hrms.organization.application.service.employee.task;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeeProbationPassedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布試用期轉正事件 Task (Integration Task)
 * 發布領域事件通知其他服務 (Payroll)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishProbationPassedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();

        UUID employeeIdUuid = UUID.fromString(context.getEmployeeId());

        EmployeeProbationPassedEvent event = new EmployeeProbationPassedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                LocalDate.now());

        eventPublisher.publishEvent(event);

        log.info("試用期轉正事件發布: {}", employee.getFullName());
    }

    @Override
    public String getName() {
        return "發布轉正事件";
    }
}
