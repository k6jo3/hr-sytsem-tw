package com.company.hrms.organization.application.service.employee.task;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeeEmailChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布員工 Email 變更事件 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishEmployeeEmailChangedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        if (employee == null) {
            log.warn("Employee is null in context, skipping event publishing");
            return;
        }

        // 檢查 Email 是否有變更
        String oldEmail = (String) context.getAttribute("oldEmail");
        String newEmail = employee.getCompanyEmail() != null ? employee.getCompanyEmail().getValue() : null;

        if (oldEmail != null && newEmail != null && !oldEmail.equals(newEmail)) {
            EmployeeEmailChangedEvent event = new EmployeeEmailChangedEvent(
                    employee.getId().getValue(),
                    employee.getEmployeeNumber(),
                    employee.getFullName(),
                    oldEmail,
                    newEmail);

            eventPublisher.publishEvent(event);
            log.info("已發布員工 Email 變更事件: {}", event);
        }
    }

    @Override
    public String getName() {
        return "發布員工 Email 變更事件";
    }
}
