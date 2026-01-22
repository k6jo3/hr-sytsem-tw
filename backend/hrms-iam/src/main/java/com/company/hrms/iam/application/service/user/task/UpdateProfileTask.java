package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;

@Component("userUpdateProfileTask")
public class UpdateProfileTask implements PipelineTask<UserPipelineContext> {

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        var request = context.getUpdateRequest();
        var user = context.getUser();

        user.updateProfile(request.getEmail(), request.getDisplayName());
    }
}
