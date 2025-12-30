package com.company.hrms.iam.application.service.auth.task;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理員重設密碼 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminResetPasswordTask implements PipelineTask<AuthContext> {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@$!%*?&";
    private static final int TEMP_PASSWORD_LENGTH = 12;

    private final PasswordHashingDomainService passwordHashingService;
    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();
        var request = context.getAdminResetPasswordRequest();

        // 決定新密碼
        String newPassword;
        String temporaryPassword = null;
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            newPassword = request.getNewPassword();
        } else {
            newPassword = generateTemporaryPassword();
            temporaryPassword = newPassword;
        }

        // 更新密碼
        String hashedPassword = passwordHashingService.hash(newPassword);
        user.resetPassword(hashedPassword);

        // 設定是否需要下次登入時變更密碼
        if (request.isMustChangePassword()) {
            user.setMustChangePassword(true);
        }

        // 儲存更新
        userRepository.update(user);

        // 儲存臨時密碼到 Context
        context.setAttribute("temporaryPassword", temporaryPassword);

        log.info("管理員重設密碼成功: userId={}", user.getId().getValue());
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(TEMP_PASSWORD_LENGTH);

        password.append(randomChar(random, "ABCDEFGHJKLMNPQRSTUVWXYZ"));
        password.append(randomChar(random, "abcdefghjkmnpqrstuvwxyz"));
        password.append(randomChar(random, "23456789"));
        password.append(randomChar(random, "@$!%*?&"));

        for (int i = 4; i < TEMP_PASSWORD_LENGTH; i++) {
            password.append(randomChar(random, TEMP_PASSWORD_CHARS));
        }

        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    private char randomChar(SecureRandom random, String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    @Override
    public String getName() {
        return "管理員重設密碼";
    }
}
