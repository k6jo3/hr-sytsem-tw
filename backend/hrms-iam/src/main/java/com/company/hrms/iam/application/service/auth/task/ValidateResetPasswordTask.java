package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.valueobject.Password;

import lombok.RequiredArgsConstructor;

/**
 * 驗證新密碼格式 Task (for ResetPassword)
 */
@Component("validateResetPasswordTask")
@RequiredArgsConstructor
public class ValidateResetPasswordTask implements PipelineTask<AuthContext> {

    @Override
    public void execute(AuthContext context) throws Exception {
        var request = context.getResetPasswordRequest();

        // 驗證新密碼與確認密碼一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new DomainException("PASSWORD_MISMATCH", "新密碼與確認密碼不一致");
        }

        // 驗證密碼強度
        Password.validate(request.getNewPassword());
    }

    @Override
    public String getName() {
        return "驗證新密碼格式";
    }
}
