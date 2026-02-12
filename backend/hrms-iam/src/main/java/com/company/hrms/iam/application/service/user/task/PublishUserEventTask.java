package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.event.UserCreatedEvent;
import com.company.hrms.iam.domain.model.aggregate.User;

@Component
public class PublishUserEventTask implements PipelineTask<UserPipelineContext> {

    private final EventPublisher eventPublisher;

    public PublishUserEventTask(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        User user = context.getUser();
        eventPublisher.publish(new UserCreatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail().getValue(),
                user.getDisplayName(),
                user.getEmployeeId()));
    }
}
