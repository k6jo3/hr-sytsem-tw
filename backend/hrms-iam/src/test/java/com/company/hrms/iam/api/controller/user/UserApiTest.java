package com.company.hrms.iam.api.controller.user;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.iam.api.request.user.AssignUserRolesRequest;
import com.company.hrms.iam.api.request.user.BatchDeactivateUsersRequest;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.request.user.GetUserListRequest;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.api.response.user.AssignUserRolesResponse;
import com.company.hrms.iam.api.response.user.BatchDeactivateUsersResponse;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import com.company.hrms.iam.api.response.user.UserListResponse;
import com.company.hrms.iam.application.service.user.ActivateUserServiceImpl;
import com.company.hrms.iam.application.service.user.AssignUserRolesServiceImpl;
import com.company.hrms.iam.application.service.user.BatchDeactivateUsersServiceImpl;
import com.company.hrms.iam.application.service.user.CreateUserServiceImpl;
import com.company.hrms.iam.application.service.user.DeactivateUserServiceImpl;
import com.company.hrms.iam.application.service.user.GetUserListServiceImpl;
import com.company.hrms.iam.application.service.user.GetUserServiceImpl;
import com.company.hrms.iam.application.service.user.UpdateUserServiceImpl;

/**
 * HR01 使用者管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>使用者建立、更新、啟用、停用 (Command)</li>
 * <li>角色指派、批次停用 (Command)</li>
 * <li>使用者列表查詢、單一使用者查詢 (Query)</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR01 使用者管理 API 合約測試")
class UserApiTest extends BaseApiContractTest {

        @MockBean(name = "createUserServiceImpl")
        private CreateUserServiceImpl createUserService;

        @MockBean(name = "updateUserServiceImpl")
        private UpdateUserServiceImpl updateUserService;

        @MockBean(name = "activateUserServiceImpl")
        private ActivateUserServiceImpl activateUserService;

        @MockBean(name = "deactivateUserServiceImpl")
        private DeactivateUserServiceImpl deactivateUserService;

        @MockBean(name = "assignUserRolesServiceImpl")
        private AssignUserRolesServiceImpl assignUserRolesService;

        @MockBean(name = "batchDeactivateUsersServiceImpl")
        private BatchDeactivateUsersServiceImpl batchDeactivateUsersService;

        @MockBean(name = "getUserListServiceImpl")
        private GetUserListServiceImpl getUserListService;

        @MockBean(name = "getUserServiceImpl")
        private GetUserServiceImpl getUserService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("admin-user");
                mockUser.setUsername("admin");
                mockUser.setRoles(Collections.singletonList("ADMIN"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                // 新增使用者管理相關權限
                authorities.add(new SimpleGrantedAuthority("user:create"));
                authorities.add(new SimpleGrantedAuthority("user:update"));
                authorities.add(new SimpleGrantedAuthority("user:activate"));
                authorities.add(new SimpleGrantedAuthority("user:deactivate"));
                authorities.add(new SimpleGrantedAuthority("user:assign-role"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 使用者命令 API 測試
         */
        @Nested
        @DisplayName("使用者命令 API")
        class UserCommandApiTests {

                @Test
                @DisplayName("IAM_USER_001: 新增使用者 - 應回傳使用者 ID")
                void createUser_ShouldReturnUserId() throws Exception {
                        // Arrange
                        CreateUserRequest request = CreateUserRequest.builder()
                                        .username("newuser")
                                        .email("newuser@example.com")
                                        .password("Password123!")
                                        .displayName("New User")
                                        .build();

                        CreateUserResponse response = CreateUserResponse.builder()
                                        .userId("user-001")
                                        .username("newuser")
                                        .message("使用者建立成功")
                                        .build();

                        when(createUserService.execCommand(any(CreateUserRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/users", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value("user-001"))
                                        .andExpect(jsonPath("$.username").value("newuser"))
                                        .andExpect(jsonPath("$.message").isNotEmpty());
                }

                @Test
                @DisplayName("IAM_USER_002: 更新使用者 - 應回傳更新後的使用者資訊")
                void updateUser_ShouldReturnUpdatedUser() throws Exception {
                        // Arrange
                        String userId = "user-001";
                        UpdateUserRequest request = UpdateUserRequest.builder()
                                        .email("updated@example.com")
                                        .displayName("Updated User")
                                        .build();

                        UserDetailResponse response = UserDetailResponse.builder()
                                        .userId(userId)
                                        .username("testuser")
                                        .email("updated@example.com")
                                        .displayName("Updated User")
                                        .status("ACTIVE")
                                        .roles(Arrays.asList("USER"))
                                        .updatedAt(LocalDateTime.now())
                                        .build();

                        when(updateUserService.execCommand(any(UpdateUserRequest.class), any(JWTModel.class),
                                        eq(userId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/users/" + userId, request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(userId))
                                        .andExpect(jsonPath("$.email").value("updated@example.com"))
                                        .andExpect(jsonPath("$.displayName").value("Updated User"));
                }

                @Test
                @DisplayName("IAM_USER_003: 啟用使用者 - 應回傳 204")
                void activateUser_ShouldReturn204() throws Exception {
                        // Arrange
                        String userId = "user-001";
                        doNothing().when(activateUserService).execCommand(isNull(), any(JWTModel.class), eq(userId));

                        // Act & Assert
                        performPut("/api/v1/users/" + userId + "/activate", null)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("IAM_USER_004: 停用使用者 - 應回傳 204")
                void deactivateUser_ShouldReturn204() throws Exception {
                        // Arrange
                        String userId = "user-001";
                        doNothing().when(deactivateUserService).execCommand(isNull(), any(JWTModel.class), eq(userId));

                        // Act & Assert
                        performPut("/api/v1/users/" + userId + "/deactivate", null)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("IAM_USER_005: 指派角色給使用者 - 應回傳角色列表")
                void assignUserRoles_ShouldReturnRoles() throws Exception {
                        // Arrange
                        String userId = "user-001";
                        AssignUserRolesRequest request = new AssignUserRolesRequest();
                        request.setRoleIds(Arrays.asList("role-001", "role-002"));

                        AssignUserRolesResponse response = AssignUserRolesResponse.builder()
                                        .userId(userId)
                                        .roles(Arrays.asList(
                                                        AssignUserRolesResponse.RoleInfo.builder()
                                                                        .roleId("role-001")
                                                                        .roleName("ADMIN")
                                                                        .displayName("管理員")
                                                                        .build(),
                                                        AssignUserRolesResponse.RoleInfo.builder()
                                                                        .roleId("role-002")
                                                                        .roleName("USER")
                                                                        .displayName("一般使用者")
                                                                        .build()))
                                        .build();

                        when(assignUserRolesService.execCommand(any(AssignUserRolesRequest.class), any(JWTModel.class),
                                        eq(userId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/users/" + userId + "/roles", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(userId))
                                        .andExpect(jsonPath("$.roles").isArray())
                                        .andExpect(jsonPath("$.roles.length()").value(2));
                }

                @Test
                @DisplayName("IAM_USER_006: 批次停用使用者 - 應回傳成功與失敗統計")
                void batchDeactivateUsers_ShouldReturnStatistics() throws Exception {
                        // Arrange
                        BatchDeactivateUsersRequest request = new BatchDeactivateUsersRequest();
                        request.setUserIds(Arrays.asList("user-001", "user-002", "user-003"));

                        BatchDeactivateUsersResponse response = BatchDeactivateUsersResponse.builder()
                                        .successIds(Arrays.asList("user-001", "user-002"))
                                        .failedUsers(Collections.singletonList(
                                                        BatchDeactivateUsersResponse.FailedUser.builder()
                                                                        .userId("user-003")
                                                                        .reason("使用者不存在")
                                                                        .build()))
                                        .successCount(2)
                                        .failedCount(1)
                                        .build();

                        when(batchDeactivateUsersService.execCommand(any(BatchDeactivateUsersRequest.class),
                                        any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/users/batch-deactivate", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.successCount").value(2))
                                        .andExpect(jsonPath("$.failedCount").value(1))
                                        .andExpect(jsonPath("$.successIds").isArray())
                                        .andExpect(jsonPath("$.failedUsers").isArray());
                }
        }

        /**
         * 使用者查詢 API 測試
         */
        @Nested
        @DisplayName("使用者查詢 API")
        class UserQueryApiTests {

                @Test
                @DisplayName("IAM_USER_007: 查詢使用者列表 - 應回傳列表結果")
                void getUserList_ShouldReturnList() throws Exception {
                        // Arrange
                        UserListResponse user1 = UserListResponse.builder()
                                        .userId("user-001")
                                        .username("user1")
                                        .email("user1@example.com")
                                        .displayName("User One")
                                        .status("ACTIVE")
                                        .build();

                        UserListResponse user2 = UserListResponse.builder()
                                        .userId("user-002")
                                        .username("user2")
                                        .email("user2@example.com")
                                        .displayName("User Two")
                                        .status("ACTIVE")
                                        .build();

                        PageResponse<UserListResponse> response = PageResponse.<UserListResponse>builder()
                                        .items(Arrays.asList(user1, user2))
                                        .page(1)
                                        .size(100)
                                        .total(2L)
                                        .totalPages(1)
                                        .build();

                        when(getUserListService.getResponse(any(GetUserListRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/users")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$.length()").value(2))
                                        .andExpect(jsonPath("$[0].userId").value("user-001"));
                }

                @Test
                @DisplayName("IAM_USER_008: 查詢單一使用者 - 應回傳使用者詳情")
                void getUser_ShouldReturnUserDetail() throws Exception {
                        // Arrange
                        String userId = "user-001";

                        UserDetailResponse response = UserDetailResponse.builder()
                                        .userId(userId)
                                        .username("testuser")
                                        .email("test@example.com")
                                        .displayName("Test User")
                                        .status("ACTIVE")
                                        .roles(Arrays.asList("USER", "MANAGER"))
                                        .lastLoginAt(LocalDateTime.now().minusHours(1))
                                        .createdAt(LocalDateTime.now().minusDays(30))
                                        .updatedAt(LocalDateTime.now())
                                        .build();

                        when(getUserService.getResponse(any(HR01UserQryController.GetUserRequest.class),
                                        any(JWTModel.class),
                                        eq(userId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/users/" + userId)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(userId))
                                        .andExpect(jsonPath("$.username").value("testuser"))
                                        .andExpect(jsonPath("$.email").value("test@example.com"))
                                        .andExpect(jsonPath("$.roles").isArray());
                }

                @Test
                @DisplayName("IAM_USER_009: 依狀態篩選使用者列表")
                void getUserList_WithStatusFilter_ShouldFilterByStatus() throws Exception {
                        // Arrange
                        UserListResponse activeUser = UserListResponse.builder()
                                        .userId("user-001")
                                        .username("activeuser")
                                        .email("active@example.com")
                                        .displayName("Active User")
                                        .status("ACTIVE")
                                        .build();

                        PageResponse<UserListResponse> response = PageResponse.<UserListResponse>builder()
                                        .items(Collections.singletonList(activeUser))
                                        .page(1)
                                        .size(100)
                                        .total(1L)
                                        .totalPages(1)
                                        .build();

                        when(getUserListService.getResponse(any(GetUserListRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/users?status=ACTIVE")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$[0].status").value("ACTIVE"));
                }
        }
}
