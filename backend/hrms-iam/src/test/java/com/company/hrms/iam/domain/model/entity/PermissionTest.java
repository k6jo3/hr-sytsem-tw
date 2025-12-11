package com.company.hrms.iam.domain.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Permission 實體測試")
class PermissionTest {

    @Test
    @DisplayName("應該成功建立 Permission")
    void shouldCreatePermissionSuccessfully() {
        // Given
        String code = "user:create";
        String name = "建立使用者";
        String description = "允許建立新使用者";

        // When
        Permission permission = Permission.create(code, name, description);

        // Then
        assertNotNull(permission.getId());
        assertEquals(code, permission.getPermissionCode());
        assertEquals(name, permission.getPermissionName());
        assertEquals(description, permission.getDescription());
        assertEquals("user", permission.getResource());
        assertEquals("create", permission.getAction());
        assertNotNull(permission.getCreatedAt());
    }

    @Test
    @DisplayName("Permission code 不含冒號應該拋出例外")
    void shouldThrowExceptionForInvalidCodeWithoutColon() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                Permission.create("usercreate", "建立使用者", "描述"));
    }

    @Test
    @DisplayName("Permission code 為 null 應該拋出例外")
    void shouldThrowExceptionForNullCode() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                Permission.create(null, "建立使用者", "描述"));
    }

    @Test
    @DisplayName("Permission code 格式不正確應該拋出例外")
    void shouldThrowExceptionForInvalidCodeFormat() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                Permission.create("user:create:extra", "建立使用者", "描述"));
    }

    @Test
    @DisplayName("應該成功更新 Permission")
    void shouldUpdatePermissionSuccessfully() {
        // Given
        Permission permission = Permission.create("user:create", "建立使用者", "原始描述");

        // When
        permission.update("新使用者權限", "更新後的描述");

        // Then
        assertEquals("新使用者權限", permission.getPermissionName());
        assertEquals("更新後的描述", permission.getDescription());
    }

    @Test
    @DisplayName("isForResource 應該正確判斷資源")
    void shouldCorrectlyCheckResource() {
        // Given
        Permission permission = Permission.create("user:create", "建立使用者", "描述");

        // Then
        assertTrue(permission.isForResource("user"));
        assertFalse(permission.isForResource("role"));
    }

    @Test
    @DisplayName("isForAction 應該正確判斷操作")
    void shouldCorrectlyCheckAction() {
        // Given
        Permission permission = Permission.create("user:create", "建立使用者", "描述");

        // Then
        assertTrue(permission.isForAction("create"));
        assertFalse(permission.isForAction("delete"));
    }

    @Test
    @DisplayName("reconstitute 應該正確重建 Permission")
    void shouldReconstitutePermissionCorrectly() {
        // Given
        String id = "perm-001";
        String code = "user:read";
        String name = "查詢使用者";
        String description = "描述";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        // When
        Permission permission = Permission.reconstitute(id, code, name, description, "user", "read", createdAt);

        // Then
        assertEquals(id, permission.getId().getValue());
        assertEquals(code, permission.getPermissionCode());
        assertEquals(name, permission.getPermissionName());
        assertEquals(description, permission.getDescription());
        assertEquals("user", permission.getResource());
        assertEquals("read", permission.getAction());
        assertEquals(createdAt, permission.getCreatedAt());
    }

    @Test
    @DisplayName("相同 ID 的 Permission 應該相等")
    void shouldBeEqualForSameId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Permission permission1 = Permission.reconstitute("perm-001", "user:read", "查詢", "描述", "user", "read", now);
        Permission permission2 = Permission.reconstitute("perm-001", "user:create", "建立", "其他", "user", "create", now);

        // Then
        assertEquals(permission1, permission2);
        assertEquals(permission1.hashCode(), permission2.hashCode());
    }
}
