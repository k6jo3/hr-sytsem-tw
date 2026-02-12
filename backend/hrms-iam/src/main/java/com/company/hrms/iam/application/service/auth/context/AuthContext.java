package com.company.hrms.iam.application.service.auth.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.domain.model.aggregate.User;

/**
 * 認證 Pipeline Context
 */
public class AuthContext extends PipelineContext {

    public AuthContext() {
    }

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

    public LoginRequest getLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;
    }

    public RefreshTokenRequest getRefreshTokenRequest() {
        return refreshTokenRequest;
    }

    public void setRefreshTokenRequest(RefreshTokenRequest refreshTokenRequest) {
        this.refreshTokenRequest = refreshTokenRequest;
    }

    public ForgotPasswordRequest getForgotPasswordRequest() {
        return forgotPasswordRequest;
    }

    public void setForgotPasswordRequest(ForgotPasswordRequest forgotPasswordRequest) {
        this.forgotPasswordRequest = forgotPasswordRequest;
    }

    public ResetPasswordRequest getResetPasswordRequest() {
        return resetPasswordRequest;
    }

    public void setResetPasswordRequest(ResetPasswordRequest resetPasswordRequest) {
        this.resetPasswordRequest = resetPasswordRequest;
    }

    public AdminResetPasswordRequest getAdminResetPasswordRequest() {
        return adminResetPasswordRequest;
    }

    public void setAdminResetPasswordRequest(AdminResetPasswordRequest adminResetPasswordRequest) {
        this.adminResetPasswordRequest = adminResetPasswordRequest;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
