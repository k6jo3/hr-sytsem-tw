package com.company.hrms.organization.application.service.department.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.event.DepartmentManagerChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Department;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布部門主管變更事件 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishDepartmentManagerChangedEventTask implements PipelineTask<DepartmentContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        Department department = context.getDepartment();
        if (department == null) {
            log.warn("Department is null in context, skipping event publishing");
            return;
        }

        // 取得新舊主管 ID
        UUID newManagerId = department.getManagerId() != null ? department.getManagerId().getValue() : null;

        // 從 Context 取得舊主管 ID (需在 AssignManagerTask 中設定)
        String oldManagerIdStr = (String) context.getAttribute("oldManagerId");
        UUID oldManagerId = oldManagerIdStr != null ? UUID.fromString(oldManagerIdStr) : null;

        if (newManagerId == null) {
            log.warn("New manager ID is null, skipping event publishing");
            return;
        }

        DepartmentManagerChangedEvent event = new DepartmentManagerChangedEvent(
                department.getId().getValue(),
                department.getCode(),
                department.getName(),
                oldManagerId,
                newManagerId);

        eventPublisher.publishEvent(event);
        log.info("已發布部門主管變更事件: {}", event);
    }

    @Override
    public String getName() {
        return "發布部門主管變更事件";
    }
}
