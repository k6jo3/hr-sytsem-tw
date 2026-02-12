package com.company.hrms.iam.api.controller.role;

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
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.iam.api.request.role.AssignPermissionsRequest;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.request.role.GetRoleListRequest;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.api.response.role.CreateRoleResponse;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.application.service.role.ActivateRoleServiceImpl;
import com.company.hrms.iam.application.service.role.AssignPermissionsServiceImpl;
import com.company.hrms.iam.application.service.role.CreateRoleServiceImpl;
import com.company.hrms.iam.application.service.role.DeactivateRoleServiceImpl;
import com.company.hrms.iam.application.service.role.DeleteRoleServiceImpl;
import com.company.hrms.iam.application.service.role.GetRoleListServiceImpl;
import com.company.hrms.iam.application.service.role.GetRoleServiceImpl;
import com.company.hrms.iam.application.service.role.GetSystemRolesServiceImpl;
import com.company.hrms.iam.application.service.role.UpdateRoleServiceImpl;

/**
 * HR01 角色管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>角色建立、更新、刪除、啟用、停用 (Command)</li>
 * <li>權限指派 (Command)</li>
 * <li>角色列表查詢、單一角色查詢、系統角色查詢 (Query)</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR01 角色管理 API 合約測試")
class RoleApiTest extends BaseApiContractTest {

        @MockBean(name = "createRoleServiceImpl")
        private CreateRoleServiceImpl createRoleService;

        @MockBean(name = "updateRoleServiceImpl")
        private UpdateRoleServiceImpl updateRoleService;

        @MockBean(name = "activateRoleServiceImpl")
        private ActivateRoleServiceImpl activateRoleService;

        @MockBean(name = "deactivateRoleServiceImpl")
        private DeactivateRoleServiceImpl deactivateRoleService;

        @MockBean(name = "deleteRoleServiceImpl")
        private DeleteRoleServiceImpl deleteRoleService;

        @MockBean(name = "assignPermissionsServiceImpl")
        private AssignPermissionsServiceImpl assignPermissionsService;

        @MockBean(name = "getRoleListServiceImpl")
        private GetRoleListServiceImpl getRoleListService;

        @MockBean(name = "getRoleServiceImpl")
        private GetRoleServiceImpl getRoleService;

        @MockBean(name = "getSystemRolesServiceImpl")
        private GetSystemRolesServiceImpl getSystemRolesService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("admin-user");
                mockUser.setUsername("admin");
                mockUser.setRoles(Collections.singletonList("ADMIN"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                // 新增角色管理相關權限
                authorities.add(new SimpleGrantedAuthority("role:create"));
                authorities.add(new SimpleGrantedAuthority("role:read"));
                authorities.add(new SimpleGrantedAuthority("role:update"));
                authorities.add(new SimpleGrantedAuthority("role:delete"));
                authorities.add(new SimpleGrantedAuthority("role:assign-permission"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 角色命令 API 測試
         */
        @Nested
        @DisplayName("角色命令 API")
        class RoleCommandApiTests {

                @Test
                @DisplayName("IAM_ROLE_001: 新增角色 - 應回傳角色 ID")
                void createRole_ShouldReturnRoleId() throws Exception {
                        // Arrange
                        CreateRoleRequest request = CreateRoleRequest.builder()
                                        .roleName("人資管理員")
                                        .roleCode("HR_ADMIN")
                                        .description("負責人資管理相關作業")
                                        .permissionIds(Arrays.asList("perm-001", "perm-002"))
                                        .build();

                        CreateRoleResponse response = CreateRoleResponse.builder()
                                        .roleId("role-001")
                                        .roleCode("HR_ADMIN")
                                        .roleName("人資管理員")
                                        .build();

                        when(createRoleService.execCommand(any(CreateRoleRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/roles", request)
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.roleId").value("role-001"))
                                        .andExpect(jsonPath("$.roleCode").value("HR_ADMIN"))
                                        .andExpect(jsonPath("$.roleName").value("人資管理員"));
                }

                @Test
                @DisplayName("IAM_ROLE_002: 更新角色 - 應回傳更新後的角色資訊")
                void updateRole_ShouldReturnUpdatedRole() throws Exception {
                        // Arrange
                        String roleId = "role-001";
                        UpdateRoleRequest request = UpdateRoleRequest.builder()
                                        .roleName("進階人資管理員")
                                        .description("負責進階人資管理相關作業")
                                        .permissionIds(Arrays.asList("perm-001", "perm-002", "perm-003"))
                                        .build();

                        RoleDetailResponse response = RoleDetailResponse.builder()
                                        .roleId(roleId)
                                        .roleName("進階人資管理員")
                                        .roleCode("HR_ADMIN")
                                        .description("負責進階人資管理相關作業")
                                        .status("ACTIVE")
                                        .isSystemRole(false)
                                        .permissionDetails(Arrays.asList(
                                                        RoleDetailResponse.PermissionItem.builder()
                                                                        .permissionId("perm-001")
                                                                        .permissionCode("user:read")
                                                                        .permissionName("讀取使用者")
                                                                        .build()))
                                        .updatedAt(LocalDateTime.now())
                                        .build();

                        when(updateRoleService.execCommand(any(UpdateRoleRequest.class), any(JWTModel.class),
                                        eq(roleId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/roles/" + roleId, request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.roleId").value(roleId))
                                        .andExpect(jsonPath("$.roleName").value("進階人資管理員"))
                                        .andExpect(jsonPath("$.status").value("ACTIVE"));
                }

                @Test
                @DisplayName("IAM_ROLE_003: 啟用角色 - 應回傳 204")
                void activateRole_ShouldReturn204() throws Exception {
                        // Arrange
                        String roleId = "role-001";
                        RoleDetailResponse response = RoleDetailResponse.builder()
                                        .roleId(roleId)
                                        .roleName("測試角色")
                                        .roleCode("TEST_ROLE")
                                        .status("ACTIVE")
                                        .build();
                        when(activateRoleService.execCommand(isNull(), any(JWTModel.class), eq(roleId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/roles/" + roleId + "/activate", null)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("IAM_ROLE_004: 停用角色 - 應回傳 204")
                void deactivateRole_ShouldReturn204() throws Exception {
                        // Arrange
                        String roleId = "role-001";
                        RoleDetailResponse response = RoleDetailResponse.builder()
                                        .roleId(roleId)
                                        .roleName("測試角色")
                                        .roleCode("TEST_ROLE")
                                        .status("INACTIVE")
                                        .build();
                        when(deactivateRoleService.execCommand(isNull(), any(JWTModel.class), eq(roleId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/roles/" + roleId + "/deactivate", null)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("IAM_ROLE_005: 刪除角色 - 應回傳 204")
                void deleteRole_ShouldReturn204() throws Exception {
                        // Arrange
                        String roleId = "role-001";
                        doNothing().when(deleteRoleService).execCommand(isNull(), any(JWTModel.class), eq(roleId));

                        // Act & Assert
                        performDelete("/api/v1/roles/" + roleId)
                                        .andExpect(status().isNoContent());
                }

                @Test
                @DisplayName("IAM_ROLE_006: 指派權限給角色 - 應回傳 204")
                void assignPermissions_ShouldReturn204() throws Exception {
                        // Arrange
                        String roleId = "role-001";
                        AssignPermissionsRequest request = new AssignPermissionsRequest(
                                        Arrays.asList("perm-001", "perm-002", "perm-003"));

                        doNothing().when(assignPermissionsService).execCommand(
                                        any(AssignPermissionsRequest.class),
                                        any(JWTModel.class),
                                        eq(roleId));

                        // Act & Assert
                        performPut("/api/v1/roles/" + roleId + "/permissions", request)
                                        .andExpect(status().isNoContent());
                }
        }

        /**
         * 角色查詢 API 測試
         */
        @Nested
        @DisplayName("角色查詢 API")
        class RoleQueryApiTests {

                @Test
                @DisplayName("IAM_ROLE_007: 查詢角色列表 - 應回傳列表結果")
                void getRoleList_ShouldReturnList() throws Exception {
                        // Arrange
                        RoleListResponse role1 = RoleListResponse.builder()
                                        .roleId("role-001")
                                        .roleName("管理員")
                                        .roleCode("ADMIN")
                                        .description("系統管理員")
                                        .isSystemRole(true)
                                        .status("ACTIVE")
                                        .permissionCount(20)
                                        .build();

                        RoleListResponse role2 = RoleListResponse.builder()
                                        .roleId("role-002")
                                        .roleName("一般使用者")
                                        .roleCode("USER")
                                        .description("一般使用者角色")
                                        .isSystemRole(true)
                                        .status("ACTIVE")
                                        .permissionCount(5)
                                        .build();

                        when(getRoleListService.getResponse(any(GetRoleListRequest.class),
                                        any(JWTModel.class)))
                                        .thenReturn(Arrays.asList(role1, role2));

                        // Act & Assert
                        performGet("/api/v1/roles")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$.length()").value(2))
                                        .andExpect(jsonPath("$[0].roleId").value("role-001"))
                                        .andExpect(jsonPath("$[0].roleCode").value("ADMIN"));
                }

                @Test
                @DisplayName("IAM_ROLE_008: 查詢單一角色 - 應回傳角色詳情")
                void getRole_ShouldReturnRoleDetail() throws Exception {
                        // Arrange
                        String roleId = "role-001";

                        RoleDetailResponse response = RoleDetailResponse.builder()
                                        .roleId(roleId)
                                        .roleName("管理員")
                                        .roleCode("ADMIN")
                                        .description("系統管理員角色")
                                        .tenantId("tenant-001")
                                        .isSystemRole(true)
                                        .status("ACTIVE")
                                        .permissions(Arrays.asList("user:read", "user:create"))
                                        .permissionDetails(Arrays.asList(
                                                        RoleDetailResponse.PermissionItem.builder()
                                                                        .permissionId("perm-001")
                                                                        .permissionCode("user:read")
                                                                        .permissionName("讀取使用者")
                                                                        .build(),
                                                        RoleDetailResponse.PermissionItem.builder()
                                                                        .permissionId("perm-002")
                                                                        .permissionCode("user:create")
                                                                        .permissionName("建立使用者")
                                                                        .build()))
                                        .createdAt(LocalDateTime.now().minusDays(30))
                                        .updatedAt(LocalDateTime.now())
                                        .build();

                        when(getRoleService.getResponse(any(HR01RoleQryController.GetRoleRequest.class),
                                        any(JWTModel.class),
                                        eq(roleId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performGet("/api/v1/roles/" + roleId)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data.roleId").value(roleId))
                                        .andExpect(jsonPath("$.data.roleName").value("管理員"))
                                        .andExpect(jsonPath("$.data.roleCode").value("ADMIN"))
                                        .andExpect(jsonPath("$.data.systemRole").value(true))
                                        .andExpect(jsonPath("$.data.permissions").isArray());
                }

                @Test
                @DisplayName("IAM_ROLE_009: 查詢系統角色列表 - 應回傳系統角色")
                void getSystemRoles_ShouldReturnSystemRoles() throws Exception {
                        // Arrange
                        RoleListResponse adminRole = RoleListResponse.builder()
                                        .roleId("role-sys-001")
                                        .roleName("系統管理員")
                                        .roleCode("SYSTEM_ADMIN")
                                        .description("最高權限系統管理員")
                                        .isSystemRole(true)
                                        .status("ACTIVE")
                                        .permissionCount(50)
                                        .build();

                        RoleListResponse userRole = RoleListResponse.builder()
                                        .roleId("role-sys-002")
                                        .roleName("預設使用者")
                                        .roleCode("DEFAULT_USER")
                                        .description("系統預設使用者角色")
                                        .isSystemRole(true)
                                        .status("ACTIVE")
                                        .permissionCount(3)
                                        .build();

                        when(getSystemRolesService.getResponse(any(HR01RoleQryController.GetSystemRolesRequest.class),
                                        any(JWTModel.class)))
                                        .thenReturn(Arrays.asList(adminRole, userRole));

                        // Act & Assert
                        performGet("/api/v1/roles/system")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$.length()").value(2))
                                        .andExpect(jsonPath("$[0].systemRole").value(true))
                                        .andExpect(jsonPath("$[1].systemRole").value(true));
                }

                @Test
                @DisplayName("IAM_ROLE_010: 依狀態篩選角色列表")
                void getRoleList_WithStatusFilter_ShouldFilterByStatus() throws Exception {
                        // Arrange
                        RoleListResponse activeRole = RoleListResponse.builder()
                                        .roleId("role-001")
                                        .roleName("活躍角色")
                                        .roleCode("ACTIVE_ROLE")
                                        .description("測試角色")
                                        .isSystemRole(false)
                                        .status("ACTIVE")
                                        .permissionCount(10)
                                        .build();

                        when(getRoleListService.getResponse(any(GetRoleListRequest.class),
                                        any(JWTModel.class)))
                                        .thenReturn(Collections.singletonList(activeRole));

                        // Act & Assert
                        performGet("/api/v1/roles?status=ACTIVE")
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$[0].status").value("ACTIVE"));
                }
        }
}
