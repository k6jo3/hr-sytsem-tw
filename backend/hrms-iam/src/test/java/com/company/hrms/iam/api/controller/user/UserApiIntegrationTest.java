package com.company.hrms.iam.api.controller.user;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.company.hrms.iam.api.request.user.AssignUserRolesRequest;
import com.company.hrms.iam.api.request.user.BatchDeactivateUsersRequest;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;

/**
 * User API 整合測試
 * 驗證用戶管理 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 *
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>用戶 CRUD API（新增、更新、查詢列表、查詢詳情）</li>
 * <li>用戶狀態管理 API（啟用、停用、批量停用）</li>
 * <li>角色指派 API</li>
 * <li>用戶搜尋與過濾 API</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-02-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
		"classpath:test-data/iam_base_data.sql",
		"classpath:test-data/user_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("用戶管理 API 整合測試")
class UserApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("admin");
		mockUser.setRoles(Collections.singletonList("ADMIN"));

		List<SimpleGrantedAuthority> authorities = Arrays.asList(
				new SimpleGrantedAuthority("ROLE_ADMIN"),
				new SimpleGrantedAuthority("user:create"),
				new SimpleGrantedAuthority("user:update"),
				new SimpleGrantedAuthority("user:activate"),
				new SimpleGrantedAuthority("user:deactivate"),
				new SimpleGrantedAuthority("user:assign-role"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 用戶 CRUD API 測試
	 */
	@Nested
	@DisplayName("用戶 CRUD API")
	class UserCrudApiTests {

		@Test
		@DisplayName("IAM_USER_API_001: 新增用戶 - 應返回用戶 ID")
		void IAM_USER_API_001_createUser_ShouldReturnUserId() throws Exception {
			// Given
			CreateUserRequest request = new CreateUserRequest();
			request.setUsername("newuser");
			request.setEmail("newuser@company.com");
			request.setPassword("NewUser@123");
			request.setDisplayName("New User");

			// When & Then
			var response = performPost("/api/v1/users", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("userId");
			assertThat(responseBody).contains("username");
		}

		@Test
		@DisplayName("IAM_USER_API_002: 新增用戶失敗 - 用戶名已存在應返回 409")
		void IAM_USER_API_002_createUser_DuplicateUsername_ShouldReturn409() throws Exception {
			// Given
			CreateUserRequest request = new CreateUserRequest();
			request.setUsername("admin"); // 已存在的用戶名
			request.setEmail("newadmin@company.com");
			request.setPassword("Admin@123");
			request.setDisplayName("Admin User");

			// When & Then
			performPost("/api/v1/users", request)
					.andExpect(status().isConflict());
		}

		@Test
		@DisplayName("IAM_USER_API_003: 更新用戶 - 應返回更新後的用戶資訊")
		void IAM_USER_API_003_updateUser_ShouldReturnUpdatedUser() throws Exception {
			// Given
			String userId = "user-001";
			UpdateUserRequest request = new UpdateUserRequest();
			request.setDisplayName("Updated Display Name");
			request.setEmail("updated@company.com");

			// When & Then
			var response = performPut("/api/v1/users/" + userId, request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("userId");
			assertThat(responseBody).contains("Updated Display Name");
		}

		@Test
		@DisplayName("IAM_USER_API_004: 更新用戶失敗 - 用戶不存在應返回 404")
		void IAM_USER_API_004_updateUser_UserNotFound_ShouldReturn404() throws Exception {
			// Given
			String userId = "NON-EXISTENT-USER";
			UpdateUserRequest request = new UpdateUserRequest();
			request.setDisplayName("Updated Name");

			// When & Then
			performPut("/api/v1/users/" + userId, request)
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("IAM_USER_API_005: 查詢用戶列表 - 應返回用戶清單")
		void IAM_USER_API_005_getUserList_ShouldReturnUsers() throws Exception {
			// When & Then
			var response = performGet("/api/v1/users")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
			assertThat(responseBody).contains("total");
		}

		@Test
		@DisplayName("IAM_USER_API_006: 查詢用戶詳情 - 應返回完整用戶資訊")
		void IAM_USER_API_006_getUserDetail_ShouldReturnUserDetail() throws Exception {
			// Given
			String userId = "user-001";

			// When & Then
			var response = performGet("/api/v1/users/" + userId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("userId");
			assertThat(responseBody).contains("username");
			assertThat(responseBody).contains("email");
			assertThat(responseBody).contains("roles");
		}

		@Test
		@DisplayName("IAM_USER_API_007: 查詢用戶詳情失敗 - 用戶不存在應返回 404")
		void IAM_USER_API_007_getUserDetail_UserNotFound_ShouldReturn404() throws Exception {
			// Given
			String userId = "NON-EXISTENT-USER";

			// When & Then
			performGet("/api/v1/users/" + userId)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 用戶狀態管理 API 測試
	 */
	@Nested
	@DisplayName("用戶狀態管理 API")
	class UserStatusApiTests {

		@Test
		@DisplayName("IAM_USER_API_008: 啟用用戶 - 應返回 204")
		void IAM_USER_API_008_activateUser_ShouldReturn204() throws Exception {
			// Given
			String userId = "user-002"; // 假設此用戶處於停用狀態

			// When & Then
			performPut("/api/v1/users/" + userId + "/activate", null)
					.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("IAM_USER_API_009: 停用用戶 - 應返回 204")
		void IAM_USER_API_009_deactivateUser_ShouldReturn204() throws Exception {
			// Given
			String userId = "user-001";

			// When & Then
			performPut("/api/v1/users/" + userId + "/deactivate", null)
					.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("IAM_USER_API_010: 批量停用用戶 - 應返回成功結果")
		void IAM_USER_API_010_batchDeactivateUsers_ShouldReturnResult() throws Exception {
			// Given
			BatchDeactivateUsersRequest request = new BatchDeactivateUsersRequest();
			request.setUserIds(Arrays.asList("user-001", "user-002"));

			// When & Then
			var response = performPut("/api/v1/users/batch-deactivate", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("successCount");
			assertThat(responseBody).contains("failedCount");
		}
	}

	/**
	 * 角色指派 API 測試
	 */
	@Nested
	@DisplayName("角色指派 API")
	class RoleAssignmentApiTests {

		@Test
		@DisplayName("IAM_USER_API_011: 指派角色給用戶 - 應返回成功訊息")
		void IAM_USER_API_011_assignRoles_ShouldReturnSuccess() throws Exception {
			// Given
			String userId = "user-001";
			AssignUserRolesRequest request = new AssignUserRolesRequest();
			request.setRoleIds(Arrays.asList("ROLE-001", "ROLE-002"));

			// When & Then
			var response = performPut("/api/v1/users/" + userId + "/roles", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("userId");
			assertThat(responseBody).contains("roles");
		}

		@Test
		@DisplayName("IAM_USER_API_012: 指派角色失敗 - 用戶不存在應返回 404")
		void IAM_USER_API_012_assignRoles_UserNotFound_ShouldReturn404() throws Exception {
			// Given
			String userId = "NON-EXISTENT-USER";
			AssignUserRolesRequest request = new AssignUserRolesRequest();
			request.setRoleIds(Arrays.asList("ROLE-001"));

			// When & Then
			performPut("/api/v1/users/" + userId + "/roles", request)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 用戶搜尋與過濾 API 測試
	 */
	@Nested
	@DisplayName("用戶搜尋與過濾 API")
	class UserSearchApiTests {

		@Test
		@DisplayName("IAM_USER_API_013: 依用戶名搜尋 - 應返回符合的用戶")
		void IAM_USER_API_013_searchByUsername_ShouldReturnMatchingUsers() throws Exception {
			// When & Then
			var response = performGet("/api/v1/users?username=admin")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("admin");
		}

		@Test
		@DisplayName("IAM_USER_API_014: 依狀態過濾 - 應返回指定狀態的用戶")
		void IAM_USER_API_014_filterByStatus_ShouldReturnFilteredUsers() throws Exception {
			// When & Then
			var response = performGet("/api/v1/users?status=ACTIVE")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("IAM_USER_API_015: 分頁查詢 - 應返回分頁結果")
		void IAM_USER_API_015_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/users?page=0&size=10")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
			assertThat(responseBody).contains("total");
			assertThat(responseBody).contains("size");
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當新增用戶請求缺少必填欄位")
		void shouldReturn400WhenCreateUserRequestMissingFields() throws Exception {
			// Given
			CreateUserRequest request = new CreateUserRequest();
			// username, email, password 都未設定

			// When & Then
			performPost("/api/v1/users", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當 Email 格式不正確")
		void shouldReturn400WhenEmailFormatInvalid() throws Exception {
			// Given
			CreateUserRequest request = new CreateUserRequest();
			request.setUsername("testuser");
			request.setEmail("invalid-email-format");
			request.setPassword("Password@123");
			request.setDisplayName("Test User");

			// When & Then
			performPost("/api/v1/users", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當密碼不符合強度要求")
		void shouldReturn400WhenPasswordTooWeak() throws Exception {
			// Given
			CreateUserRequest request = new CreateUserRequest();
			request.setUsername("testuser");
			request.setEmail("test@company.com");
			request.setPassword("weak"); // 弱密碼
			request.setDisplayName("Test User");

			// When & Then
			performPost("/api/v1/users", request)
					.andExpect(status().isBadRequest());
		}
	}
}
