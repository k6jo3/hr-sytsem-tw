package com.company.hrms.iam.application.service.profile.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新個人資料 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateProfileTask implements PipelineTask<ProfileContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(ProfileContext context) throws Exception {
        var user = context.getUser();
        var request = context.getUpdateProfileRequest();

        // 更新個人資料
        user.updateProfile(request.getEmail(), request.getDisplayName());

        // 儲存
        userRepository.update(user);

        log.info("個人資料更新成功: userId={}", user.getId().getValue());
    }

    @Override
    public String getName() {
        return "更新個人資料";
    }
}
