package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.event.RoleUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布角色更新事件
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishRoleUpdatedEventTask implements PipelineTask<RoleContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(RoleContext context) {
        RoleUpdatedEvent event = new RoleUpdatedEvent(context.getRoleId());
        eventPublisher.publish(event);
    }

    @Override
    public String getName() {
        return "發布角色更新事件";
    }
}
