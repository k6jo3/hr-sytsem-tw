package com.company.hrms.iam.application.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.response.auth.RefreshTokenResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.GenerateTokenTask;
import com.company.hrms.iam.application.service.auth.task.LoadUserByIdForAuthTask;
import com.company.hrms.iam.application.service.auth.task.ValidateRefreshTokenTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刷新 Token Application Service (Pipeline 模式)
 */
@Service("refreshTokenServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenServiceImpl
        implements CommandApiService<RefreshTokenRequest, RefreshTokenResponse> {

    private final ValidateRefreshTokenTask validateRefreshTokenTask;
    private final LoadUserByIdForAuthTask loadUserByIdForAuthTask;
    private final GenerateTokenTask generateTokenTask;

    @Value("${jwt.access-token-expiry:3600000}")
    private long accessTokenExpiry;

    @Override
    public RefreshTokenResponse execCommand(RefreshTokenRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("刷新 Token");

        AuthContext context = new AuthContext(request);

        BusinessPipeline.start(context)
                .next(validateRefreshTokenTask)
                .next(loadUserByIdForAuthTask)
                .next(generateTokenTask)
                .execute();

        return RefreshTokenResponse.builder()
                .accessToken(context.getAccessToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000)
                .build();
    }
}
