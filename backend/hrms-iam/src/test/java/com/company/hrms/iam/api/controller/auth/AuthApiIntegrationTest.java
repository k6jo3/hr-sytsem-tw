package com.company.hrms.iam.api.controller.auth;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;
import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;

/**
 * Auth API 整合測試
 * 驗證認證相關 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p><b>TODO:</b> 測試資料腳本缺失，需建立以下檔案才能啟用測試：
 * <ul>
 *   <li><b>iam_base_data.sql:</b>
 *     <ul>
 *       <li>建立基礎資料表結構（roles, permissions, role_permissions）</li>
 *       <li>插入測試用的角色資料（ADMIN, USER, HR 等）</li>
 *       <li>插入測試用的權限資料</li>
 *     </ul>
 *   </li>
 *   <li><b>user_test_data.sql:</b>
 *     <ul>
 *       <li>插入測試用的用戶資料（admin, test-user, deactivated_user 等）</li>
 *       <li>用戶應包含不同狀態（ACTIVE, INACTIVE）</li>
 *       <li>用戶應包含密碼 hash（BCrypt 加密）</li>
 *       <li>用戶應關聯到角色</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p><b>測試涵蓋範圍:</b>
 * <ul>
 *   <li>登入 API（成功、失敗、帳號鎖定）</li>
 *   <li>Token 刷新 API</li>
 *   <li>登出 API</li>
 *   <li>忘記密碼 API</li>
 *   <li>重設密碼 API</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-02-03
 */
@Disabled("TODO: 缺少測試資料腳本 (iam_base_data.sql, user_test_data.sql)，需建立後啟用測試")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
		"classpath:test-data/iam_base_data.sql",
		"classpath:test-data/user_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("認證 API 整合測試")
class AuthApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("admin");
		mockUser.setRoles(Collections.singletonList("ADMIN"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(java.util.stream.Collectors.toList());

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 登入 API 測試
	 */
	@Nested
	@DisplayName("登入 API")
	class LoginApiTests {

		@Test
		@DisplayName("IAM_AUTH_API_001: 登入成功 - 應返回 accessToken 和 refreshToken")
		void IAM_AUTH_API_001_login_ShouldReturnTokens() throws Exception {
			// Given
			LoginRequest request = new LoginRequest();
			request.setUsername("admin");
			request.setPassword("Admin@123");

			// When & Then
			var response = performPost("/api/v1/auth/login", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("accessToken");
			assertThat(responseBody).contains("refreshToken");
			assertThat(responseBody).contains("expiresIn");
		}

		@Test
		@DisplayName("IAM_AUTH_API_002: 登入失敗 - 密碼錯誤應返回 401")
		void IAM_AUTH_API_002_login_WrongPassword_ShouldReturn401() throws Exception {
			// Given
			LoginRequest request = new LoginRequest();
			request.setUsername("admin");
			request.setPassword("WrongPassword");

			// When & Then
			performPost("/api/v1/auth/login", request)
					.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("IAM_AUTH_API_003: 登入失敗 - 用戶不存在應返回 401")
		void IAM_AUTH_API_003_login_UserNotFound_ShouldReturn401() throws Exception {
			// Given
			LoginRequest request = new LoginRequest();
			request.setUsername("nonexistent");
			request.setPassword("Password@123");

			// When & Then
			performPost("/api/v1/auth/login", request)
					.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("IAM_AUTH_API_004: 登入失敗 - 帳號停用應返回 423")
		void IAM_AUTH_API_004_login_DeactivatedUser_ShouldReturn423() throws Exception {
			// Given
			LoginRequest request = new LoginRequest();
			request.setUsername("deactivated_user");
			request.setPassword("User@123");

			// When & Then
			performPost("/api/v1/auth/login", request)
					.andExpect(status().isLocked());
		}
	}

	/**
	 * Token 刷新 API 測試
	 */
	@Nested
	@DisplayName("Token 刷新 API")
	class RefreshTokenApiTests {

		@Test
		@DisplayName("IAM_AUTH_API_005: 刷新 Token 成功 - 應返回新的 accessToken")
		void IAM_AUTH_API_005_refreshToken_ShouldReturnNewToken() throws Exception {
			// Given
			RefreshTokenRequest request = new RefreshTokenRequest();
			request.setRefreshToken("valid-refresh-token");

			// When & Then
			var response = performPost("/api/v1/auth/refresh-token", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("accessToken");
			assertThat(responseBody).contains("expiresIn");
		}

		@Test
		@DisplayName("IAM_AUTH_API_006: 刷新 Token 失敗 - 無效的 refreshToken 應返回 401")
		void IAM_AUTH_API_006_refreshToken_InvalidToken_ShouldReturn401() throws Exception {
			// Given
			RefreshTokenRequest request = new RefreshTokenRequest();
			request.setRefreshToken("invalid-token");

			// When & Then
			performPost("/api/v1/auth/refresh-token", request)
					.andExpect(status().isUnauthorized());
		}
	}

	/**
	 * 登出 API 測試
	 */
	@Nested
	@DisplayName("登出 API")
	class LogoutApiTests {

		@Test
		@DisplayName("IAM_AUTH_API_007: 登出成功 - 應返回 204")
		void IAM_AUTH_API_007_logout_ShouldReturn204() throws Exception {
			// When & Then
			performPost("/api/v1/auth/logout", null)
					.andExpect(status().isNoContent());
		}
	}

	/**
	 * 忘記密碼 API 測試
	 */
	@Nested
	@DisplayName("忘記密碼 API")
	class ForgotPasswordApiTests {

		@Test
		@DisplayName("IAM_AUTH_API_008: 忘記密碼 - 應發送重置郵件")
		void IAM_AUTH_API_008_forgotPassword_ShouldSendEmail() throws Exception {
			// Given
			ForgotPasswordRequest request = new ForgotPasswordRequest();
			request.setEmail("admin@company.com");

			// When & Then
			var response = performPost("/api/v1/auth/forgot-password", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("message");
		}

		@Test
		@DisplayName("IAM_AUTH_API_009: 忘記密碼 - Email 不存在也應返回成功（安全考量）")
		void IAM_AUTH_API_009_forgotPassword_NonExistentEmail_ShouldStillReturn200() throws Exception {
			// Given
			ForgotPasswordRequest request = new ForgotPasswordRequest();
			request.setEmail("nonexistent@company.com");

			// When & Then
			performPost("/api/v1/auth/forgot-password", request)
					.andExpect(status().isOk());
		}
	}

	/**
	 * 重設密碼 API 測試
	 */
	@Nested
	@DisplayName("重設密碼 API")
	class ResetPasswordApiTests {

		@Test
		@DisplayName("IAM_AUTH_API_010: 重設密碼成功 - 應返回成功訊息")
		void IAM_AUTH_API_010_resetPassword_ShouldReturnSuccess() throws Exception {
			// Given
			ResetPasswordRequest request = new ResetPasswordRequest();
			request.setCurrentPassword("Admin@123");
			request.setNewPassword("NewPassword@123");
			request.setConfirmPassword("NewPassword@123");

			// When & Then
			var response = performPost("/api/v1/auth/reset-password", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("success");
		}

		@Test
		@DisplayName("IAM_AUTH_API_011: 重設密碼失敗 - 當前密碼錯誤應返回 401")
		void IAM_AUTH_API_011_resetPassword_WrongCurrentPassword_ShouldReturn401() throws Exception {
			// Given
			ResetPasswordRequest request = new ResetPasswordRequest();
			request.setCurrentPassword("WrongPassword");
			request.setNewPassword("NewPassword@123");
			request.setConfirmPassword("NewPassword@123");

			// When & Then
			performPost("/api/v1/auth/reset-password", request)
					.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("IAM_AUTH_API_012: 重設密碼失敗 - 新密碼不符合規則應返回 400")
		void IAM_AUTH_API_012_resetPassword_WeakPassword_ShouldReturn400() throws Exception {
			// Given
			ResetPasswordRequest request = new ResetPasswordRequest();
			request.setCurrentPassword("Admin@123");
			request.setNewPassword("weak");
			request.setConfirmPassword("weak");

			// When & Then
			performPost("/api/v1/auth/reset-password", request)
					.andExpect(status().isBadRequest());
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當登入請求缺少必填欄位")
		void shouldReturn400WhenLoginRequestMissingFields() throws Exception {
			// Given
			LoginRequest request = new LoginRequest();
			// username 和 password 都未設定

			// When & Then
			performPost("/api/v1/auth/login", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當重設密碼請求的兩次密碼不一致")
		void shouldReturn400WhenPasswordsDoNotMatch() throws Exception {
			// Given
			ResetPasswordRequest request = new ResetPasswordRequest();
			request.setCurrentPassword("Admin@123");
			request.setNewPassword("NewPassword@123");
			request.setConfirmPassword("DifferentPassword@123");

			// When & Then
			performPost("/api/v1/auth/reset-password", request)
					.andExpect(status().isBadRequest());
		}
	}
}
