package com.company.hrms.iam.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleId 值物件測試")
class RoleIdTest {

    @Test
    @DisplayName("應該成功建立 RoleId")
    void shouldCreateRoleIdSuccessfully() {
        // Given
        String value = "role-001";

        // When
        RoleId roleId = new RoleId(value);

        // Then
        assertEquals(value, roleId.getValue());
    }

    @Test
    @DisplayName("使用 of 方法應該成功建立 RoleId")
    void shouldCreateRoleIdUsingOfMethod() {
        // Given
        String value = "role-002";

        // When
        RoleId roleId = RoleId.of(value);

        // Then
        assertEquals(value, roleId.getValue());
    }

    @Test
    @DisplayName("generate 方法應該產生不同的 RoleId")
    void shouldGenerateUniqueRoleIds() {
        // When
        RoleId roleId1 = RoleId.generate();
        RoleId roleId2 = RoleId.generate();

        // Then
        assertNotEquals(roleId1, roleId2);
        assertNotNull(roleId1.getValue());
        assertNotNull(roleId2.getValue());
    }

    @Test
    @DisplayName("null 值應該拋出例外")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new RoleId(null));
    }

    @Test
    @DisplayName("空白字串應該拋出例外")
    void shouldThrowExceptionForBlankValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new RoleId(""));
        assertThrows(IllegalArgumentException.class, () -> new RoleId("   "));
    }

    @Test
    @DisplayName("相同值的 RoleId 應該相等")
    void shouldBeEqualForSameValue() {
        // Given
        RoleId roleId1 = new RoleId("role-001");
        RoleId roleId2 = new RoleId("role-001");

        // Then
        assertEquals(roleId1, roleId2);
        assertEquals(roleId1.hashCode(), roleId2.hashCode());
    }

    @Test
    @DisplayName("toString 應該回傳值")
    void shouldReturnValueOnToString() {
        // Given
        RoleId roleId = new RoleId("role-001");

        // Then
        assertEquals("role-001", roleId.toString());
    }
}
