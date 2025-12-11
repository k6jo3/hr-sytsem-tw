package com.company.hrms.iam.application.service.auth;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * 管理員重設使用者密碼 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：adminResetPassword</p>
 */
@Service("adminResetPasswordServiceImpl")
@Transactional
public class AdminResetPasswordServiceImpl implements CommandApiService<AdminResetPasswordRequest, ResetPasswordResponse> {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@$!%*?&";
    private static final int TEMP_PASSWORD_LENGTH = 12;

    private final IUserRepository userRepository;
    private final PasswordHashingDomainService passwordHashingService;

    @Autowired
    public AdminResetPasswordServiceImpl(IUserRepository userRepository, PasswordHashingDomainService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Override
    public ResetPasswordResponse execCommand(AdminResetPasswordRequest request, JWTModel currentUser, String... args) throws Exception {
        String targetUserId = args[0];

        // 查詢目標使用者
        User user = userRepository.findById(new UserId(targetUserId))
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "使用者不存在"));

        // 決定新密碼 (若未提供則產生臨時密碼)
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

        // TODO: 若 sendNotification 為 true，發送通知給使用者

        return ResetPasswordResponse.builder()
                .success(true)
                .message("使用者密碼已重設")
                .temporaryPassword(temporaryPassword)
                .build();
    }

    /**
     * 產生臨時密碼
     */
    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(TEMP_PASSWORD_LENGTH);

        // 確保至少有一個大寫、小寫、數字和特殊字元
        password.append(randomChar(random, "ABCDEFGHJKLMNPQRSTUVWXYZ"));
        password.append(randomChar(random, "abcdefghjkmnpqrstuvwxyz"));
        password.append(randomChar(random, "23456789"));
        password.append(randomChar(random, "@$!%*?&"));

        // 填充剩餘字元
        for (int i = 4; i < TEMP_PASSWORD_LENGTH; i++) {
            password.append(randomChar(random, TEMP_PASSWORD_CHARS));
        }

        // 打亂順序
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
}
