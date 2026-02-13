package com.company.hrms.organization.application.service.department.task;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.event.DepartmentCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Department;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布部門建立事件 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishDepartmentCreatedEventTask implements PipelineTask<DepartmentContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        Department department = context.getDepartment();
        if (department == null) {
            log.warn("Department is null in context, skipping event publishing");
            return;
        }

        DepartmentCreatedEvent event = new DepartmentCreatedEvent(
                department.getId().getValue(),
                department.getCode(),
                department.getName(),
                department.getOrganizationId().getValue(),
                department.getParentId() != null ? department.getParentId().getValue() : null,
                department.getLevel());

        eventPublisher.publishEvent(event);
        log.info("已發布部門建立事件: {}", event);
    }

    @Override
    public String getName() {
        return "發布部門建立事件";
    }
}
