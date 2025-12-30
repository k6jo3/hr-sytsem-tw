package com.company.hrms.organization.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;

/**
 * Department 聚合根單元測試
 */
@DisplayName("Department 聚合根測試")
class DepartmentTest {

    private static final UUID ORG_ID = UUID.randomUUID();

    @Nested
    @DisplayName("建立部門")
    class CreateDepartmentTests {

        @Test
        @DisplayName("應成功建立部門，並產生唯一 ID")
        void shouldCreateDepartmentWithUniqueId() {
            // When
            Department dept = Department.create(
                    ORG_ID,
                    "DEPT001",
                    "研發部",
                    null);

            // Then
            assertNotNull(dept.getId());
            assertNotNull(dept.getId().getValue());
            assertEquals("DEPT001", dept.getCode());
            assertEquals("研發部", dept.getName());
            assertEquals(ORG_ID.toString(), dept.getOrganizationId().getValue().toString());
            assertNull(dept.getParentId());
        }

        @Test
        @DisplayName("初始狀態應為 ACTIVE")
        void shouldCreateWithActiveStatus() {
            // When
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);

            // Then
            assertEquals(DepartmentStatus.ACTIVE, dept.getStatus());
            assertTrue(dept.isActive());
        }

        @Test
        @DisplayName("應成功建立子部門")
        void shouldCreateChildDepartment() {
            // Given
            Department parent = Department.create(ORG_ID, "DEPT001", "研發部", null);

            // When
            Department child = Department.create(
                    ORG_ID,
                    "DEPT001-01",
                    "前端組",
                    parent.getId().getValue().toString());

            // Then
            assertNotNull(child.getParentId());
            assertEquals(parent.getId().getValue(), child.getParentId().getValue());
        }

        @Test
        @DisplayName("代碼為空時應拋出例外")
        void shouldThrowExceptionWhenCodeIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Department.create(ORG_ID, "", "研發部", null));
            assertEquals("DEPT_CODE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("名稱為空時應拋出例外")
        void shouldThrowExceptionWhenNameIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Department.create(ORG_ID, "DEPT001", "", null));
            assertEquals("DEPT_NAME_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("組織 ID 為空時應拋出例外")
        void shouldThrowExceptionWhenOrganizationIdIsNull() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Department.create(null, "DEPT001", "研發部", null));
            assertEquals("ORG_ID_REQUIRED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("更新部門")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("應成功更新部門資訊")
        void shouldUpdateDepartment() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "舊名稱", null);

            // When
            dept.update("新名稱", "部門描述");

            // Then
            assertEquals("新名稱", dept.getName());
            assertEquals("部門描述", dept.getDescription());
        }

        @Test
        @DisplayName("已停用部門更新應拋出例外")
        void shouldThrowExceptionWhenDeactivated() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "名稱", null);
            dept.deactivate();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, () -> dept.update("新名稱", null));
            assertEquals("DEPT_DEACTIVATED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("指派主管")
    class AssignManagerTests {

        @Test
        @DisplayName("應成功指派主管")
        void shouldAssignManager() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);
            UUID managerId = UUID.randomUUID();

            // When
            dept.assignManager(managerId);

            // Then
            assertEquals(managerId.toString(), dept.getManagerId().getValue().toString());
        }

        @Test
        @DisplayName("應成功移除主管")
        void shouldRemoveManager() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);
            dept.assignManager(UUID.randomUUID());

            // When
            dept.assignManager(null);

            // Then
            assertNull(dept.getManagerId());
        }
    }

    @Nested
    @DisplayName("移動部門")
    class MoveDepartmentTests {

        @Test
        @DisplayName("應成功移動到新父部門")
        void shouldMoveToNewParent() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);
            UUID newParentId = UUID.randomUUID();

            // When
            dept.moveTo(newParentId);

            // Then
            assertEquals(newParentId.toString(), dept.getParentId().getValue().toString());
        }

        @Test
        @DisplayName("應成功移動到根層級")
        void shouldMoveToRoot() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", UUID.randomUUID().toString());

            // When
            dept.moveTo(null);

            // Then
            assertNull(dept.getParentId());
        }
    }

    @Nested
    @DisplayName("停用部門")
    class DeactivateDepartmentTests {

        @Test
        @DisplayName("應成功停用部門")
        void shouldDeactivateDepartment() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);

            // When
            dept.deactivate();

            // Then
            assertEquals(DepartmentStatus.INACTIVE, dept.getStatus());
            assertFalse(dept.isActive());
        }

        @Test
        @DisplayName("已停用部門再次停用應拋出例外")
        void shouldThrowExceptionWhenAlreadyDeactivated() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);
            dept.deactivate();

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    dept::deactivate);
            assertEquals("ALREADY_DEACTIVATED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("排序")
    class SortOrderTests {

        @Test
        @DisplayName("應成功更新排序順序")
        void shouldUpdateSortOrder() {
            // Given
            Department dept = Department.create(ORG_ID, "DEPT001", "研發部", null);

            // When
            dept.updateSortOrder(10);

            // Then
            assertEquals(10, dept.getSortOrder());
        }
    }

    @Nested
    @DisplayName("相等性")
    class EqualityTests {

        @Test
        @DisplayName("相同 ID 的部門應視為相等")
        void shouldBeEqualWhenSameId() {
            // Given
            Department dept1 = Department.create(ORG_ID, "DEPT001", "名稱1", null);

            Department dept2 = Department.builder()
                    .id(dept1.getId())
                    .organizationId(new OrganizationId(ORG_ID.toString()))
                    .code("DEPT002")
                    .name("名稱2")
                    .status(DepartmentStatus.ACTIVE)
                    .build();

            // Then
            assertEquals(dept1, dept2);
            assertEquals(dept1.hashCode(), dept2.hashCode());
        }
    }
}
