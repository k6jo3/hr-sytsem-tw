package com.company.hrms.iam.application.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.CheckUserStatusTask;
import com.company.hrms.iam.application.service.auth.task.GenerateTokenTask;
import com.company.hrms.iam.application.service.auth.task.LoadUserByUsernameTask;
import com.company.hrms.iam.application.service.auth.task.RecordLoginTask;
import com.company.hrms.iam.application.service.auth.task.ValidatePasswordTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登入 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: POST /api/v1/auth/login
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>LoadUserByUsernameTask - 載入使用者</li>
 * <li>CheckUserStatusTask - 檢查使用者狀態</li>
 * <li>ValidatePasswordTask - 驗證密碼</li>
 * <li>RecordLoginTask - 記錄登入</li>
 * <li>GenerateTokenTask - 產生 JWT Token</li>
 * </ol>
 */
@Service("loginServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginServiceImpl
        implements CommandApiService<LoginRequest, LoginResponse> {

    private final LoadUserByUsernameTask loadUserByUsernameTask;
    private final CheckUserStatusTask checkUserStatusTask;
    private final ValidatePasswordTask validatePasswordTask;
    private final RecordLoginTask recordLoginTask;
    private final GenerateTokenTask generateTokenTask;

    @Value("${jwt.access-token-expiry:3600000}")
    private long accessTokenExpiry;

    @Override
    public LoginResponse execCommand(LoginRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("使用者登入: username={}", request.getUsername());

        // 1. 建立 Context
        AuthContext context = new AuthContext(request);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadUserByUsernameTask)
                .next(checkUserStatusTask)
                .next(validatePasswordTask)
                .next(recordLoginTask)
                .next(generateTokenTask)
                .execute();

        // 3. 建立回應
        var user = context.getUser();

        return LoginResponse.builder()
                .accessToken(context.getAccessToken())
                .refreshToken(context.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000)
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getId().getValue())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail().getValue())
                        .employeeId(user.getEmployeeId())
                        .roles(user.getRoles())
                        .build())
                .build();
    }
}
