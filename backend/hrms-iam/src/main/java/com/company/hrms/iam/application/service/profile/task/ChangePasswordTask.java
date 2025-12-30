package com.company.hrms.iam.application.service.profile.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 變更密碼 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordTask implements PipelineTask<ProfileContext> {

    private final PasswordHashingDomainService passwordHashingService;
    private final IUserRepository userRepository;

    @Override
    public void execute(ProfileContext context) throws Exception {
        var request = context.getChangePasswordRequest();
        var user = context.getUser();

        // 雜湊新密碼
        String newPasswordHash = passwordHashingService.hash(request.getNewPassword());

        // 變更密碼
        user.changePassword(newPasswordHash);

        // 儲存
        userRepository.update(user);

        log.info("密碼變更成功: userId={}", user.getId().getValue());
    }

    @Override
    public String getName() {
        return "變更密碼";
    }
}
