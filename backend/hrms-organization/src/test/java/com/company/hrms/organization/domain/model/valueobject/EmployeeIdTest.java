package com.company.hrms.organization.domain.model.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * EmployeeId 值物件單元測試
 */
@DisplayName("EmployeeId 值物件測試")
class EmployeeIdTest {

    @Nested
    @DisplayName("建立 EmployeeId")
    class CreateEmployeeIdTests {

        @Test
        @DisplayName("應成功從字串建立 EmployeeId")
        void shouldCreateFromString() {
            // Given
            String uuidString = "123e4567-e89b-12d3-a456-426614174000";

            // When
            EmployeeId id = new EmployeeId(uuidString);

            // Then
            assertNotNull(id);
            assertEquals(uuidString, id.getValue());
        }

        @Test
        @DisplayName("應成功產生新的 EmployeeId")
        void shouldGenerateNewId() {
            // When
            EmployeeId id = EmployeeId.generate();

            // Then
            assertNotNull(id);
            assertNotNull(id.getValue());
            assertDoesNotThrow(() -> UUID.fromString(id.getValue()));
        }

        @Test
        @DisplayName("每次產生的 ID 應該是唯一的")
        void shouldGenerateUniqueIds() {
            // When
            EmployeeId id1 = EmployeeId.generate();
            EmployeeId id2 = EmployeeId.generate();

            // Then
            assertNotEquals(id1, id2);
            assertNotEquals(id1.getValue(), id2.getValue());
        }
    }

    @Nested
    @DisplayName("相等性")
    class EqualityTests {

        @Test
        @DisplayName("相同 ID 值應視為相等")
        void shouldBeEqualForSameId() {
            // Given
            String uuidString = "123e4567-e89b-12d3-a456-426614174000";
            EmployeeId id1 = new EmployeeId(uuidString);
            EmployeeId id2 = new EmployeeId(uuidString);

            // Then
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("不同 ID 值應視為不相等")
        void shouldNotBeEqualForDifferentIds() {
            // Given
            EmployeeId id1 = new EmployeeId("123e4567-e89b-12d3-a456-426614174000");
            EmployeeId id2 = new EmployeeId("223e4567-e89b-12d3-a456-426614174001");

            // Then
            assertNotEquals(id1, id2);
        }
    }

    @Nested
    @DisplayName("字串表示")
    class ToStringTests {

        @Test
        @DisplayName("toString 應回傳 ID 值")
        void shouldReturnIdValue() {
            // Given
            String uuidString = "123e4567-e89b-12d3-a456-426614174000";
            EmployeeId id = new EmployeeId(uuidString);

            // Then
            assertEquals(uuidString, id.toString());
        }
    }
}
