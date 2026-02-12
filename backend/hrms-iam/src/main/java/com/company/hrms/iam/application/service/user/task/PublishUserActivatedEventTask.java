package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.event.UserActivatedEvent;
import com.company.hrms.iam.domain.model.aggregate.User;

/**
 * 發布使用者啟用事件 Task
 */
@Component
public class PublishUserActivatedEventTask implements PipelineTask<UserPipelineContext> {

    private final EventPublisher eventPublisher;

    public PublishUserActivatedEventTask(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        User user = context.getUser();
        eventPublisher.publish(new UserActivatedEvent(user.getId(), user.getUsername()));
    }
}
