package com.company.hrms.iam.application.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.response.auth.RefreshTokenResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

/**
 * 重新整理 Token Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：refreshToken
 * </p>
 */
@Service("refreshTokenServiceImpl")
@Transactional
public class RefreshTokenServiceImpl
        implements CommandApiService<RefreshTokenRequest, RefreshTokenResponse> {

    private final IUserRepository userRepository;
    private final JwtTokenDomainService jwtTokenService;

    @Value("${jwt.access-token-expiry:3600000}")
    private long accessTokenExpiry;

    public RefreshTokenServiceImpl(IUserRepository userRepository,
            JwtTokenDomainService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RefreshTokenResponse execCommand(RefreshTokenRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        String refreshToken = request.getRefreshToken();

        // 1. 驗證 Refresh Token
        if (!jwtTokenService.validateToken(refreshToken)) {
            throw new DomainException("INVALID_REFRESH_TOKEN", "Refresh Token 無效或已過期");
        }

        // 2. 從 Token 中提取用戶 ID
        String userId = jwtTokenService.extractUserId(refreshToken);

        // 3. 查詢用戶
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "使用者不存在"));

        // 4. 檢查用戶狀態
        if (!user.isActive()) {
            throw new DomainException("USER_INACTIVE", "使用者帳號已停用");
        }

        // 5. 產生新的 Access Token
        String newAccessToken = jwtTokenService.generateAccessToken(user);

        // 6. 建立回應
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000) // 轉換為秒
                .build();
    }
}
