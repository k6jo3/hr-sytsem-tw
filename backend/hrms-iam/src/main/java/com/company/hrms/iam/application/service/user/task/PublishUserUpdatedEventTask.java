package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.event.UserUpdatedEvent;
import com.company.hrms.iam.domain.model.aggregate.User;

@Component
public class PublishUserUpdatedEventTask implements PipelineTask<UserPipelineContext> {

    private final EventPublisher eventPublisher;

    public PublishUserUpdatedEventTask(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        User user = context.getUser();
        eventPublisher.publish(new UserUpdatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail().getValue(),
                user.getDisplayName(),
                user.getEmployeeId()));
    }
}
