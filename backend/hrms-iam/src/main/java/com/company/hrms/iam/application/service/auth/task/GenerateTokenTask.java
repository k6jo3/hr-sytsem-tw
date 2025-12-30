package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

import lombok.RequiredArgsConstructor;

/**
 * 產生 JWT Token Task
 */
@Component
@RequiredArgsConstructor
public class GenerateTokenTask implements PipelineTask<AuthContext> {

    private final JwtTokenDomainService jwtTokenService;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();

        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        context.setAccessToken(accessToken);
        context.setRefreshToken(refreshToken);
    }

    @Override
    public String getName() {
        return "產生 JWT Token";
    }
}
