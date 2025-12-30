package com.company.hrms.iam.application.service.profile.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.domain.model.valueobject.Password;

import lombok.RequiredArgsConstructor;

/**
 * 驗證新密碼格式 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateNewPasswordTask implements PipelineTask<ProfileContext> {

    @Override
    public void execute(ProfileContext context) throws Exception {
        var request = context.getChangePasswordRequest();

        // 驗證確認密碼
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new DomainException("PASSWORD_MISMATCH", "新密碼與確認密碼不相符");
        }

        // 驗證密碼強度
        Password.validate(request.getNewPassword());
    }

    @Override
    public String getName() {
        return "驗證新密碼格式";
    }
}
