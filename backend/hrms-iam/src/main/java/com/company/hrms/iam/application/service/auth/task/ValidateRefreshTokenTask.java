package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

import lombok.RequiredArgsConstructor;

/**
 * 驗證 Refresh Token Task
 */
@Component
@RequiredArgsConstructor
public class ValidateRefreshTokenTask implements PipelineTask<AuthContext> {

    private final JwtTokenDomainService jwtTokenService;

    @Override
    public void execute(AuthContext context) throws Exception {
        var request = context.getRefreshTokenRequest();
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenService.validateToken(refreshToken)) {
            throw new DomainException("INVALID_REFRESH_TOKEN", "Refresh Token 無效或已過期");
        }

        // 提取用戶 ID
        String userId = jwtTokenService.extractUserId(refreshToken);
        context.setUserId(userId);
    }

    @Override
    public String getName() {
        return "驗證 Refresh Token";
    }
}
