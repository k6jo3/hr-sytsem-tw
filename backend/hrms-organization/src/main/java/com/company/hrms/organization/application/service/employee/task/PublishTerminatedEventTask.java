package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
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

        UUID employeeIdUuid = UUID.fromString(context.getEmployeeId());
        String companyEmail = employee.getCompanyEmail() != null
                ? employee.getCompanyEmail().getValue()
                : null;

        // 取得離職類型名稱
        String terminationTypeName = employee.getTerminationType() != null
                ? employee.getTerminationType().name()
                : null;

        // 計算預告期天數
        int noticePeriodDays = employee.calculateNoticePeriod();

        EmployeeTerminatedEvent event = new EmployeeTerminatedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                companyEmail,
                employee.getOrganizationId(),
                employee.getDepartmentId(),
                employee.getTerminationDate(),
                employee.getTerminationReason(),
                terminationTypeName,
                employee.getHireDate(),
                noticePeriodDays);

        eventPublisher.publishEvent(event);

        log.info("離職事件發布: {} - {}, 類型={}, 預告期={}天",
                employee.getFullName(),
                employee.getTerminationDate(),
                terminationTypeName,
                noticePeriodDays);
    }

    @Override
    public String getName() {
        return "發布離職事件";
    }
}
