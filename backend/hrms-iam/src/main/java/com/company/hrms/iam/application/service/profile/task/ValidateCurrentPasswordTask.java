package com.company.hrms.iam.application.service.profile.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;

/**
 * 驗證目前密碼 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateCurrentPasswordTask implements PipelineTask<ProfileContext> {

    private final PasswordHashingDomainService passwordHashingService;

    @Override
    public void execute(ProfileContext context) throws Exception {
        var request = context.getChangePasswordRequest();
        var user = context.getUser();

        if (!passwordHashingService.verify(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new DomainException("INVALID_PASSWORD", "目前密碼不正確");
        }
    }

    @Override
    public String getName() {
        return "驗證目前密碼";
    }
}
