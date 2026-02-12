package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.event.RolePermissionsUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布角色權限更新事件 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishRolePermissionsUpdatedTask implements PipelineTask<RoleContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        var request = context.getAssignPermissionsRequest();

        log.info("發布角色權限更新事件: roleId={}", role.getId().getValue());

        eventPublisher.publish(new RolePermissionsUpdatedEvent(
                role.getId().getValue(),
                request.getPermissionIds()));
    }

    @Override
    public String getName() {
        return "發布權限更新事件";
    }
}
