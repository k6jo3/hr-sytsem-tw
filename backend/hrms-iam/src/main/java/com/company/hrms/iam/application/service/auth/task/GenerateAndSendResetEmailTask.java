package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.service.EmailDomainService;
import com.company.hrms.iam.domain.service.PasswordResetTokenDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 產生並發送密碼重置郵件 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateAndSendResetEmailTask implements PipelineTask<AuthContext> {

    private final PasswordResetTokenDomainService passwordResetTokenService;
    private final EmailDomainService emailService;
    private final com.company.hrms.common.domain.event.EventPublisher eventPublisher;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();

        // 只在有使用者時執行
        if (user == null) {
            return;
        }

        try {
            // 產生重置 Token
            String token = passwordResetTokenService.generateToken(user.getId().getValue());
            context.setResetToken(token);

            // 發送郵件
            emailService.sendPasswordResetEmail(
                    user.getEmail().getValue(),
                    token,
                    user.getDisplayName());

            eventPublisher.publish(new com.company.hrms.iam.domain.event.PasswordResetRequestedEvent(
                    user.getId().getValue(),
                    user.getEmail().getValue()));

            log.info("密碼重置郵件已發送，用戶: {}", user.getUsername());
        } catch (Exception e) {
            // 記錄錯誤但不暴露給前端
            log.error("發送密碼重置郵件失敗: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "產生並發送密碼重置郵件";
    }

    @Override
    public boolean shouldExecute(AuthContext context) {
        return context.getUser() != null;
    }
}
