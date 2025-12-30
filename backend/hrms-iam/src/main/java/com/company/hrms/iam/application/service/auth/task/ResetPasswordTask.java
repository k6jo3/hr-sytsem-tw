package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 重設密碼 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordTask implements PipelineTask<AuthContext> {

    private final PasswordHashingDomainService passwordHashingService;
    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();
        var request = context.getResetPasswordRequest();

        // 驗證當前密碼（如提供）
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isBlank()) {
            if (!passwordHashingService.verify(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new DomainException("INVALID_CURRENT_PASSWORD", "當前密碼不正確");
            }
        }

        // 驗證新密碼不可與舊密碼相同
        if (passwordHashingService.verify(request.getNewPassword(), user.getPasswordHash())) {
            throw new DomainException("SAME_PASSWORD", "新密碼不可與舊密碼相同");
        }

        // 更新密碼
        String hashedPassword = passwordHashingService.hash(request.getNewPassword());
        user.resetPassword(hashedPassword);

        // 儲存更新
        userRepository.update(user);

        log.info("密碼重設成功: userId={}", user.getId().getValue());
    }

    @Override
    public String getName() {
        return "重設密碼";
    }
}
