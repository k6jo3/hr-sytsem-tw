package com.company.hrms.iam.application.service.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.profile.ChangePasswordRequest;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.application.service.profile.task.ChangePasswordTask;
import com.company.hrms.iam.application.service.profile.task.LoadUserByIdTask;
import com.company.hrms.iam.application.service.profile.task.ValidateCurrentPasswordTask;
import com.company.hrms.iam.application.service.profile.task.ValidateNewPasswordTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 變更密碼 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: PUT /api/v1/profile/change-password
 * </p>
 */
@Service("changePasswordServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChangePasswordServiceImpl
        implements CommandApiService<ChangePasswordRequest, Void> {

    private final ValidateNewPasswordTask validateNewPasswordTask;
    private final LoadUserByIdTask loadUserByIdTask;
    private final ValidateCurrentPasswordTask validateCurrentPasswordTask;
    private final ChangePasswordTask changePasswordTask;

    @Override
    public Void execCommand(ChangePasswordRequest request, JWTModel currentUser, String... args)
            throws Exception {

        String userId = currentUser.getUserId();
        log.info("變更密碼: userId={}", userId);

        ProfileContext context = new ProfileContext(userId, request);

        BusinessPipeline.start(context)
                .next(validateNewPasswordTask)
                .next(loadUserByIdTask)
                .next(validateCurrentPasswordTask)
                .next(changePasswordTask)
                .execute();

        return null;
    }
}
