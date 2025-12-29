package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.repository.IUserRepository;

@Component
public class SaveUserTask implements PipelineTask<UserPipelineContext> {

    private final IUserRepository userRepository;

    public SaveUserTask(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        userRepository.save(context.getUser());
    }
}
