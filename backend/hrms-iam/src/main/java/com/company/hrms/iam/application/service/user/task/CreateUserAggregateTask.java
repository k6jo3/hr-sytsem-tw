package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.model.aggregate.User;

@Component
public class CreateUserAggregateTask implements PipelineTask<UserPipelineContext> {

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        var request = context.getCreateRequest();

        User user = User.create(
                request.getUsername(),
                request.getEmail(),
                context.getPasswordHash(),
                request.getDisplayName());

        user.activate();
        context.setUser(user);
    }
}
