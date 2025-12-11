package com.company.hrms.iam.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PermissionId 值物件測試")
class PermissionIdTest {

    @Test
    @DisplayName("應該成功建立 PermissionId")
    void shouldCreatePermissionIdSuccessfully() {
        // Given
        String value = "perm-001";

        // When
        PermissionId permissionId = new PermissionId(value);

        // Then
        assertEquals(value, permissionId.getValue());
    }

    @Test
    @DisplayName("使用 of 方法應該成功建立 PermissionId")
    void shouldCreatePermissionIdUsingOfMethod() {
        // Given
        String value = "perm-002";

        // When
        PermissionId permissionId = PermissionId.of(value);

        // Then
        assertEquals(value, permissionId.getValue());
    }

    @Test
    @DisplayName("generate 方法應該產生不同的 PermissionId")
    void shouldGenerateUniquePermissionIds() {
        // When
        PermissionId permissionId1 = PermissionId.generate();
        PermissionId permissionId2 = PermissionId.generate();

        // Then
        assertNotEquals(permissionId1, permissionId2);
        assertNotNull(permissionId1.getValue());
        assertNotNull(permissionId2.getValue());
    }

    @Test
    @DisplayName("null 值應該拋出例外")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new PermissionId(null));
    }

    @Test
    @DisplayName("空白字串應該拋出例外")
    void shouldThrowExceptionForBlankValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new PermissionId(""));
        assertThrows(IllegalArgumentException.class, () -> new PermissionId("   "));
    }

    @Test
    @DisplayName("相同值的 PermissionId 應該相等")
    void shouldBeEqualForSameValue() {
        // Given
        PermissionId permissionId1 = new PermissionId("perm-001");
        PermissionId permissionId2 = new PermissionId("perm-001");

        // Then
        assertEquals(permissionId1, permissionId2);
        assertEquals(permissionId1.hashCode(), permissionId2.hashCode());
    }

    @Test
    @DisplayName("toString 應該回傳值")
    void shouldReturnValueOnToString() {
        // Given
        PermissionId permissionId = new PermissionId("perm-001");

        // Then
        assertEquals("perm-001", permissionId.toString());
    }
}
