package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

@Component
public class HashPasswordTask implements PipelineTask<UserPipelineContext> {

    private final PasswordHashingDomainService passwordHashingService;

    public HashPasswordTask(PasswordHashingDomainService passwordHashingService) {
        this.passwordHashingService = passwordHashingService;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        String rawPassword = context.getCreateRequest().getPassword();
        String hashedPassword = passwordHashingService.hash(rawPassword);
        context.setPasswordHash(hashedPassword);
    }
}
