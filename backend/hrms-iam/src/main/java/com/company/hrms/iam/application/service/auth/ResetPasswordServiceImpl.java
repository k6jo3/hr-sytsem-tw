package com.company.hrms.iam.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

/**
 * 使用者自行重設密碼 Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：resetPassword
 * </p>
 */
@Service("resetPasswordServiceImpl")
@Transactional
public class ResetPasswordServiceImpl implements CommandApiService<ResetPasswordRequest, ResetPasswordResponse> {

    private final IUserRepository userRepository;
    private final PasswordHashingDomainService passwordHashingService;

    public ResetPasswordServiceImpl(IUserRepository userRepository,
            PasswordHashingDomainService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Override
    public ResetPasswordResponse execCommand(ResetPasswordRequest request, JWTModel currentUser, String... args)
            throws Exception {
        // 驗證使用者已登入
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new DomainException("UNAUTHORIZED", "請先登入");
        }

        // 驗證新密碼與確認密碼一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new DomainException("PASSWORD_MISMATCH", "新密碼與確認密碼不一致");
        }

        // 查詢使用者
        User user = userRepository.findById(new UserId(currentUser.getUserId()))
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "使用者不存在"));

        // 驗證當前密碼
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

        return ResetPasswordResponse.builder()
                .success(true)
                .message("密碼已成功更新")
                .build();
    }
}
