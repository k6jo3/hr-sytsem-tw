package com.company.hrms.iam.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role 聚合根測試")
class RoleTest {

    @Nested
    @DisplayName("建立角色")
    class CreateRole {

        @Test
        @DisplayName("應該成功建立租戶角色")
        void shouldCreateTenantRoleSuccessfully() {
            // Given
            String roleName = "部門主管";
            String roleCode = "DEPT_MANAGER";
            String description = "部門主管角色";
            String tenantId = "tenant-001";

            // When
            Role role = Role.create(roleName, roleCode, description, tenantId);

            // Then
            assertNotNull(role.getId());
            assertEquals(roleName, role.getRoleName());
            assertEquals(roleCode, role.getRoleCode());
            assertEquals(description, role.getDescription());
            assertEquals(tenantId, role.getTenantId());
            assertFalse(role.isSystemRole());
            assertEquals(RoleStatus.ACTIVE, role.getStatus());
            assertTrue(role.getPermissionIds().isEmpty());
            assertNotNull(role.getCreatedAt());
            assertNotNull(role.getUpdatedAt());
        }

        @Test
        @DisplayName("應該成功建立系統角色")
        void shouldCreateSystemRoleSuccessfully() {
            // Given
            String roleName = "系統管理員";
            String roleCode = "SYSTEM_ADMIN";
            String description = "系統管理員角色";

            // When
            Role role = Role.createSystemRole(roleName, roleCode, description);

            // Then
            assertNotNull(role.getId());
            assertEquals(roleName, role.getRoleName());
            assertEquals(roleCode, role.getRoleCode());
            assertNull(role.getTenantId());
            assertTrue(role.isSystemRole());
            assertEquals(RoleStatus.ACTIVE, role.getStatus());
        }

        @Test
        @DisplayName("角色代碼應該轉換為大寫")
        void shouldConvertRoleCodeToUpperCase() {
            // When
            Role role = Role.create("角色", "manager", "描述", "tenant-001");

            // Then
            assertEquals("MANAGER", role.getRoleCode());
        }

        @Test
        @DisplayName("空白角色名稱應該拋出例外")
        void shouldThrowExceptionForBlankRoleName() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Role.create("", "ROLE_CODE", "描述", "tenant-001"));
            assertEquals("ROLE_NAME_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("空白角色代碼應該拋出例外")
        void shouldThrowExceptionForBlankRoleCode() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Role.create("角色名稱", "", "描述", "tenant-001"));
            assertEquals("ROLE_CODE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("無效的角色代碼格式應該拋出例外")
        void shouldThrowExceptionForInvalidRoleCodeFormat() {
            // 包含特殊字元（破折號）
            DomainException ex1 = assertThrows(DomainException.class, () ->
                    Role.create("角色", "ROLE-CODE", "描述", "tenant-001"));
            assertEquals("INVALID_ROLE_CODE", ex1.getErrorCode());

            // 包含特殊字元（空格）
            DomainException ex2 = assertThrows(DomainException.class, () ->
                    Role.create("角色", "ROLE CODE", "描述", "tenant-001"));
            assertEquals("INVALID_ROLE_CODE", ex2.getErrorCode());
        }

        @Test
        @DisplayName("角色名稱過長應該拋出例外")
        void shouldThrowExceptionForTooLongRoleName() {
            // Given
            String longName = "a".repeat(51);

            // When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Role.create(longName, "ROLE_CODE", "描述", "tenant-001"));
            assertEquals("ROLE_NAME_TOO_LONG", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("更新角色")
    class UpdateRole {

        @Test
        @DisplayName("應該成功更新角色資訊")
        void shouldUpdateRoleSuccessfully() {
            // Given
            Role role = Role.create("原始名稱", "ROLE_CODE", "原始描述", "tenant-001");
            LocalDateTime originalUpdatedAt = role.getUpdatedAt();

            // When
            role.update("新名稱", "新描述");

            // Then
            assertEquals("新名稱", role.getRoleName());
            assertEquals("新描述", role.getDescription());
            assertTrue(role.getUpdatedAt().isAfter(originalUpdatedAt) ||
                       role.getUpdatedAt().isEqual(originalUpdatedAt));
        }

        @Test
        @DisplayName("系統角色不可修改")
        void shouldNotUpdateSystemRole() {
            // Given
            Role systemRole = Role.createSystemRole("系統管理員", "SYSTEM_ADMIN", "描述");

            // When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    systemRole.update("新名稱", "新描述"));
            assertEquals("SYSTEM_ROLE_IMMUTABLE", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("角色狀態管理")
    class RoleStatusManagement {

        @Test
        @DisplayName("應該成功啟用角色")
        void shouldActivateRoleSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            role.deactivate();

            // When
            role.activate();

            // Then
            assertEquals(RoleStatus.ACTIVE, role.getStatus());
            assertTrue(role.isActive());
        }

        @Test
        @DisplayName("應該成功停用角色")
        void shouldDeactivateRoleSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");

            // When
            role.deactivate();

            // Then
            assertEquals(RoleStatus.INACTIVE, role.getStatus());
            assertFalse(role.isActive());
        }

        @Test
        @DisplayName("系統角色不可停用")
        void shouldNotDeactivateSystemRole() {
            // Given
            Role systemRole = Role.createSystemRole("系統管理員", "SYSTEM_ADMIN", "描述");

            // When & Then
            DomainException exception = assertThrows(DomainException.class, systemRole::deactivate);
            assertEquals("SYSTEM_ROLE_IMMUTABLE", exception.getErrorCode());
        }

        @Test
        @DisplayName("應該成功軟刪除角色")
        void shouldSoftDeleteRoleSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");

            // When
            role.delete();

            // Then
            assertEquals(RoleStatus.DELETED, role.getStatus());
        }

        @Test
        @DisplayName("系統角色不可刪除")
        void shouldNotDeleteSystemRole() {
            // Given
            Role systemRole = Role.createSystemRole("系統管理員", "SYSTEM_ADMIN", "描述");

            // When & Then
            DomainException exception = assertThrows(DomainException.class, systemRole::delete);
            assertEquals("SYSTEM_ROLE_IMMUTABLE", exception.getErrorCode());
        }

        @Test
        @DisplayName("已刪除的角色不可啟用")
        void shouldNotActivateDeletedRole() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            role.delete();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, role::activate);
            assertEquals("ROLE_DELETED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("權限管理")
    class PermissionManagement {

        @Test
        @DisplayName("應該成功指派權限")
        void shouldAssignPermissionSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            PermissionId permissionId = PermissionId.of("perm-001");

            // When
            role.assignPermission(permissionId);

            // Then
            assertTrue(role.hasPermission(permissionId));
            assertEquals(1, role.getPermissionCount());
        }

        @Test
        @DisplayName("重複指派相同權限不應增加")
        void shouldNotDuplicatePermission() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            PermissionId permissionId = PermissionId.of("perm-001");

            // When
            role.assignPermission(permissionId);
            role.assignPermission(permissionId);

            // Then
            assertEquals(1, role.getPermissionCount());
        }

        @Test
        @DisplayName("應該成功批量指派權限")
        void shouldAssignMultiplePermissionsSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            List<PermissionId> permissions = Arrays.asList(
                    PermissionId.of("perm-001"),
                    PermissionId.of("perm-002"),
                    PermissionId.of("perm-003")
            );

            // When
            role.assignPermissions(permissions);

            // Then
            assertEquals(3, role.getPermissionCount());
            assertTrue(role.hasPermission(PermissionId.of("perm-001")));
            assertTrue(role.hasPermission(PermissionId.of("perm-002")));
            assertTrue(role.hasPermission(PermissionId.of("perm-003")));
        }

        @Test
        @DisplayName("應該成功移除權限")
        void shouldRemovePermissionSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            PermissionId permissionId = PermissionId.of("perm-001");
            role.assignPermission(permissionId);

            // When
            role.removePermission(permissionId);

            // Then
            assertFalse(role.hasPermission(permissionId));
            assertEquals(0, role.getPermissionCount());
        }

        @Test
        @DisplayName("應該成功清除所有權限")
        void shouldClearAllPermissionsSuccessfully() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            role.assignPermission(PermissionId.of("perm-001"));
            role.assignPermission(PermissionId.of("perm-002"));

            // When
            role.clearPermissions();

            // Then
            assertEquals(0, role.getPermissionCount());
        }

        @Test
        @DisplayName("指派 null 權限應該拋出例外")
        void shouldThrowExceptionForNullPermission() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");

            // When & Then
            assertThrows(NullPointerException.class, () -> role.assignPermission(null));
        }

        @Test
        @DisplayName("權限列表應該不可變")
        void shouldReturnImmutablePermissionList() {
            // Given
            Role role = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            role.assignPermission(PermissionId.of("perm-001"));

            // When & Then
            assertThrows(UnsupportedOperationException.class, () ->
                    role.getPermissionIds().add(PermissionId.of("perm-002")));
        }
    }

    @Nested
    @DisplayName("重建角色")
    class ReconstituteRole {

        @Test
        @DisplayName("應該正確重建角色")
        void shouldReconstituteRoleCorrectly() {
            // Given
            String id = "role-001";
            String roleName = "測試角色";
            String roleCode = "TEST_ROLE";
            String description = "測試描述";
            String tenantId = "tenant-001";
            boolean systemRole = false;
            RoleStatus status = RoleStatus.ACTIVE;
            List<PermissionId> permissions = Arrays.asList(
                    PermissionId.of("perm-001"),
                    PermissionId.of("perm-002")
            );
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            Role role = Role.reconstitute(id, roleName, roleCode, description, tenantId,
                    systemRole, status, permissions, createdAt, updatedAt);

            // Then
            assertEquals(id, role.getId().getValue());
            assertEquals(roleName, role.getRoleName());
            assertEquals(roleCode, role.getRoleCode());
            assertEquals(description, role.getDescription());
            assertEquals(tenantId, role.getTenantId());
            assertEquals(systemRole, role.isSystemRole());
            assertEquals(status, role.getStatus());
            assertEquals(2, role.getPermissionCount());
            assertEquals(createdAt, role.getCreatedAt());
            assertEquals(updatedAt, role.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同 ID 的 Role 應該相等")
        void shouldBeEqualForSameId() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Role role1 = Role.reconstitute("role-001", "角色1", "CODE1", "描述1",
                    "tenant-001", false, RoleStatus.ACTIVE, null, now, now);
            Role role2 = Role.reconstitute("role-001", "角色2", "CODE2", "描述2",
                    "tenant-002", true, RoleStatus.INACTIVE, null, now, now);

            // Then
            assertEquals(role1, role2);
            assertEquals(role1.hashCode(), role2.hashCode());
        }

        @Test
        @DisplayName("不同 ID 的 Role 不應該相等")
        void shouldNotBeEqualForDifferentId() {
            // Given
            Role role1 = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");
            Role role2 = Role.create("角色", "ROLE_CODE", "描述", "tenant-001");

            // Then
            assertNotEquals(role1, role2);
        }
    }
}
