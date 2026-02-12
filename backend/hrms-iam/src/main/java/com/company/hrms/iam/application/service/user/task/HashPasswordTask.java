package com.company.hrms.iam.application.service.user.task;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

@Component
public class HashPasswordTask implements PipelineTask<UserPipelineContext> {

    private final PasswordHashingDomainService passwordHashingService;
    private static final SecureRandom random = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public HashPasswordTask(PasswordHashingDomainService passwordHashingService) {
        this.passwordHashingService = passwordHashingService;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        String rawPassword = context.getCreateRequest().getPassword();

        // 若未提供密碼，則自動產生 8 位隨機密碼（含大小寫+數字）
        if (!StringUtils.hasText(rawPassword)) {
            rawPassword = generateRandomPassword(8);
            context.setGeneratedPassword(rawPassword);  // 儲存明文密碼供後續發送郵件使用
        }

        String hashedPassword = passwordHashingService.hash(rawPassword);
        context.setPasswordHash(hashedPassword);
    }

    /**
     * 產生隨機密碼
     */
    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
