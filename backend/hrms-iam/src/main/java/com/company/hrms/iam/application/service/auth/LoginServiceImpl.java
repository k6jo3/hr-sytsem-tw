package com.company.hrms.iam.application.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.AccountLockingDomainService;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

/**
 * 登入 Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：login
 * </p>
 */
@Service("loginServiceImpl")
@Transactional
public class LoginServiceImpl
        implements CommandApiService<LoginRequest, LoginResponse> {

    private final IUserRepository userRepository;
    private final PasswordHashingDomainService passwordHashingService;
    private final JwtTokenDomainService jwtTokenService;
    private final AccountLockingDomainService accountLockingService;

    @Value("${jwt.access-token-expiry:3600000}")
    private long accessTokenExpiry;

    public LoginServiceImpl(IUserRepository userRepository,
            PasswordHashingDomainService passwordHashingService,
            JwtTokenDomainService jwtTokenService,
            AccountLockingDomainService accountLockingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
        this.jwtTokenService = jwtTokenService;
        this.accountLockingService = accountLockingService;
    }

    /**
     * 執行登入
     *
     * @param request     登入請求
     * @param currentUser 當前登入使用者 (登入時為 null)
     * @param args        額外參數
     * @return 登入回應
     */
    @Override
    public LoginResponse execCommand(LoginRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        // 1. 查詢使用者
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DomainException("LOGIN_FAILED", "使用者名稱或密碼錯誤"));

        // 2. 檢查使用者狀態
        checkUserStatus(user);

        // 3. 驗證密碼
        if (!passwordHashingService.verify(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
        }

        // 4. 登入成功，記錄登入
        user.recordLogin();
        userRepository.update(user);

        // 5. 產生 Token
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        // 6. 建立回應
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000) // 轉換為秒
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getId().getValue())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .email(user.getEmail().getValue())
                        .roles(user.getRoles())
                        .build())
                .build();
    }

    /**
     * 檢查使用者狀態
     */
    private void checkUserStatus(User user) {
        // 檢查是否已停用
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DomainException("USER_INACTIVE", "使用者帳號已停用");
        }

        // 檢查是否已刪除
        if (user.getStatus() == UserStatus.DELETED) {
            throw new DomainException("USER_DELETED", "使用者帳號已刪除");
        }

        // 檢查是否被鎖定
        if (user.isLocked()) {
            // 嘗試自動解鎖 (如果鎖定時間已過)
            if (!accountLockingService.checkAndUnlock(user)) {
                throw new DomainException("USER_LOCKED",
                        String.format("帳號已鎖定，請於 %s 後再試", user.getLockedUntil()));
            }
            userRepository.update(user);
        }
    }

    /**
     * 處理登入失敗
     */
    private void handleFailedLogin(User user) {
        boolean locked = accountLockingService.recordFailureAndCheckLock(user);
        userRepository.update(user);

        if (locked) {
            throw new DomainException("USER_LOCKED",
                    String.format("登入失敗次數過多，帳號已鎖定 %d 分鐘",
                            accountLockingService.getLockDurationMinutes()));
        }

        int remainingAttempts = accountLockingService.getMaxFailedAttempts()
                - user.getFailedLoginAttempts();
        throw new DomainException("LOGIN_FAILED",
                String.format("使用者名稱或密碼錯誤，剩餘嘗試次數: %d", remainingAttempts));
    }
}
