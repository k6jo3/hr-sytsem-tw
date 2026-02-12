package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理員重置密碼 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminResetPasswordTask implements PipelineTask<AuthContext> {

    private final PasswordHashingDomainService passwordHashingService;
    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();
        var request = context.getAdminResetPasswordRequest();

        // 生成臨時密碼或使用請求中的新密碼
        String newPassword = request.getNewPassword();
        log.info("管理員正在重置使用者密碼: userId={}", user.getId().getValue());

        // 更新密碼雜湊
        String hashedPassword = passwordHashingService.hash(newPassword);
        user.resetPassword(hashedPassword);

        // 儲存更新
        userRepository.update(user);

        log.info("密碼重置成功（管理員操作）: userId={}", user.getId().getValue());
    }

    @Override
    public String getName() {
        return "管理員重置密碼";
    }
}
