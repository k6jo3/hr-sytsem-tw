package com.company.hrms.iam.application.service.auth.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.domain.model.aggregate.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 認證 Pipeline Context
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthContext extends PipelineContext {

    // === 輸入 ===
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private AdminResetPasswordRequest adminResetPasswordRequest;
    private String userId;

    // === 中間數據 ===
    private User user;
    private String accessToken;
    private String refreshToken;
    private String resetToken;

    // === 建構子 ===

    public AuthContext(LoginRequest request) {
        this.loginRequest = request;
    }

    public AuthContext(RefreshTokenRequest request) {
        this.refreshTokenRequest = request;
    }

    public AuthContext(ForgotPasswordRequest request) {
        this.forgotPasswordRequest = request;
    }

    public AuthContext(ResetPasswordRequest request) {
        this.resetPasswordRequest = request;
    }

    public AuthContext(AdminResetPasswordRequest request, String userId) {
        this.adminResetPasswordRequest = request;
        this.userId = userId;
    }

    public AuthContext(String userId) {
        this.userId = userId;
    }
}
