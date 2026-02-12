package com.company.hrms.iam.api.controller.auth;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.ForgotPasswordResponse;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.api.response.auth.RefreshTokenResponse;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
import com.company.hrms.iam.application.service.auth.AdminResetPasswordServiceImpl;
import com.company.hrms.iam.application.service.auth.ForgotPasswordServiceImpl;
import com.company.hrms.iam.application.service.auth.LoginServiceImpl;
import com.company.hrms.iam.application.service.auth.LogoutServiceImpl;
import com.company.hrms.iam.application.service.auth.RefreshTokenServiceImpl;
import com.company.hrms.iam.application.service.auth.ResetPasswordServiceImpl;

/**
 * HR01 認證管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>登入、登出、Token 刷新</li>
 * <li>忘記密碼、重設密碼</li>
 * <li>管理員重設密碼</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR01 認證管理 API 合約測試")
class AuthApiTest extends BaseApiContractTest {

        @MockBean(name = "loginServiceImpl")
        private LoginServiceImpl loginService;

        @MockBean(name = "logoutServiceImpl")
        private LogoutServiceImpl logoutService;

        @MockBean(name = "refreshTokenServiceImpl")
        private RefreshTokenServiceImpl refreshTokenService;

        @MockBean(name = "forgotPasswordServiceImpl")
        private ForgotPasswordServiceImpl forgotPasswordService;

        @MockBean(name = "resetPasswordServiceImpl")
        private ResetPasswordServiceImpl resetPasswordService;

        @MockBean(name = "adminResetPasswordServiceImpl")
        private AdminResetPasswordServiceImpl adminResetPasswordService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("test-user");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("ADMIN"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("user:reset-password"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 登入/登出 API 測試
         */
        @Nested
        @DisplayName("登入/登出 API")
        class AuthenticationApiTests {

                @Test
                @DisplayName("IAM_AUTH_001: 使用者登入 - 應回傳 Access Token")
                void login_ShouldReturnAccessToken() throws Exception {
                        // Arrange
                        LoginRequest request = LoginRequest.builder()
                                        .username("testuser")
                                        .password("password123")
                                        .build();

                        LoginResponse response = LoginResponse.builder()
                                        .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                        .refreshToken("refresh-token-123")
                                        .tokenType("Bearer")
                                        .expiresIn(3600L)
                                        .user(LoginResponse.UserInfo.builder()
                                                        .userId("user-001")
                                                        .username("testuser")
                                                        .displayName("Test User")
                                                        .email("test@example.com")
                                                        .roles(Arrays.asList("USER"))
                                                        .build())
                                        .build();

                        when(loginService.execCommand(any(LoginRequest.class), isNull()))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/auth/login", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.accessToken").isNotEmpty())
                                        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                                        .andExpect(jsonPath("$.tokenType").value("Bearer"))
                                        .andExpect(jsonPath("$.user.username").value("testuser"));
                }

                @Test
                @DisplayName("IAM_AUTH_002: Token 刷新 - 應回傳新 Access Token")
                void refreshToken_ShouldReturnNewAccessToken() throws Exception {
                        // Arrange
                        RefreshTokenRequest request = RefreshTokenRequest.builder()
                                        .refreshToken("old-refresh-token")
                                        .build();

                        RefreshTokenResponse response = RefreshTokenResponse.builder()
                                        .accessToken("new-access-token")
                                        .tokenType("Bearer")
                                        .expiresIn(3600L)
                                        .build();

                        when(refreshTokenService.execCommand(any(RefreshTokenRequest.class), isNull()))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/auth/refresh-token", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                                        .andExpect(jsonPath("$.tokenType").value("Bearer"));
                }

                @Test
                @DisplayName("IAM_AUTH_003: 使用者登出 - 應回傳 204")
                void logout_ShouldReturn204() throws Exception {
                        // Arrange
                        doNothing().when(logoutService).execCommand(isNull(), any(JWTModel.class));

                        // Act & Assert
                        performPost("/api/v1/auth/logout", null)
                                        .andExpect(status().isNoContent());
                }
        }

        /**
         * 密碼管理 API 測試
         */
        @Nested
        @DisplayName("密碼管理 API")
        class PasswordManagementApiTests {

                @Test
                @DisplayName("IAM_AUTH_004: 忘記密碼 - 應回傳成功訊息")
                void forgotPassword_ShouldReturnSuccessMessage() throws Exception {
                        // Arrange
                        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                                        .email("user@example.com")
                                        .build();

                        ForgotPasswordResponse response = ForgotPasswordResponse.builder()
                                        .message("若 Email 存在，重置郵件已發送")
                                        .build();

                        when(forgotPasswordService.execCommand(any(ForgotPasswordRequest.class), isNull()))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/auth/forgot-password", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("IAM_AUTH_005: 使用者變更密碼 - 應回傳成功狀態")
                void resetPassword_ShouldReturnSuccess() throws Exception {
                        // Arrange
                        ResetPasswordRequest request = ResetPasswordRequest.builder()
                                        .token("valid-reset-token")
                                        .currentPassword("OldPassword123!")
                                        .newPassword("NewPassword456!")
                                        .confirmPassword("NewPassword456!")
                                        .build();

                        ResetPasswordResponse response = ResetPasswordResponse.builder()
                                        .success(true)
                                        .message("密碼變更成功")
                                        .build();

                        when(resetPasswordService.execCommand(any(ResetPasswordRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/auth/reset-password", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true));
                }

                @Test
                @DisplayName("IAM_AUTH_006: 管理員重設使用者密碼 - 應回傳成功狀態")
                void adminResetPassword_ShouldReturnSuccess() throws Exception {
                        // Arrange
                        String userId = "user-001";
                        AdminResetPasswordRequest request = AdminResetPasswordRequest.builder()
                                        .newPassword("TempPassword123!")
                                        .forceChangeOnNextLogin(true)
                                        .build();

                        ResetPasswordResponse response = ResetPasswordResponse.builder()
                                        .success(true)
                                        .message("密碼已重設，使用者下次登入須變更密碼")
                                        .build();

                        when(adminResetPasswordService.execCommand(any(AdminResetPasswordRequest.class),
                                        any(JWTModel.class), eq(userId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/auth/users/" + userId + "/password/reset", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true));
                }
        }
}
