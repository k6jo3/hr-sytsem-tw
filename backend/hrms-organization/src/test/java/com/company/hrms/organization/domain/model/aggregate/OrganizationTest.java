package com.company.hrms.organization.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.OrganizationStatus;

/**
 * Organization 聚合根單元測試
 */
@DisplayName("Organization 聚合根測試")
class OrganizationTest {

    @Nested
    @DisplayName("建立組織")
    class CreateOrganizationTests {

        @Test
        @DisplayName("應成功建立組織，並產生唯一 ID")
        void shouldCreateOrganizationWithUniqueId() {
            // When
            Organization org = Organization.create(
                    "ORG001",
                    "台灣科技公司",
                    "Taiwan Tech Co.",
                    "12345678");

            // Then
            assertNotNull(org.getId());
            assertNotNull(org.getId().getValue());
            assertEquals("ORG001", org.getCode());
            assertEquals("台灣科技公司", org.getName());
            assertEquals("Taiwan Tech Co.", org.getNameEn());
            assertEquals("12345678", org.getTaxId());
        }

        @Test
        @DisplayName("初始狀態應為 ACTIVE")
        void shouldCreateWithActiveStatus() {
            // When
            Organization org = Organization.create(
                    "ORG001", "台灣科技公司", null, null);

            // Then
            assertEquals(OrganizationStatus.ACTIVE, org.getStatus());
            assertTrue(org.isActive());
        }

        @Test
        @DisplayName("代碼為空時應拋出例外")
        void shouldThrowExceptionWhenCodeIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Organization.create("", "台灣科技公司", null, null));
            assertEquals("ORG_CODE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("名稱為空時應拋出例外")
        void shouldThrowExceptionWhenNameIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Organization.create("ORG001", "", null, null));
            assertEquals("ORG_NAME_REQUIRED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("更新組織")
    class UpdateOrganizationTests {

        @Test
        @DisplayName("應成功更新組織資訊")
        void shouldUpdateOrganization() {
            // Given
            Organization org = Organization.create("ORG001", "舊名稱", null, null);

            // When
            org.update("新名稱", "New Name", "12345678", "地址", "02-12345678");

            // Then
            assertEquals("新名稱", org.getName());
            assertEquals("New Name", org.getNameEn());
            assertEquals("12345678", org.getTaxId());
            assertEquals("地址", org.getAddress());
            assertEquals("02-12345678", org.getPhone());
        }

        @Test
        @DisplayName("已停用組織更新應拋出例外")
        void shouldThrowExceptionWhenDeactivated() {
            // Given
            Organization org = Organization.create("ORG001", "名稱", null, null);
            org.deactivate();

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> org.update("新名稱", null, null, null, null));
            assertEquals("ORG_DEACTIVATED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("停用組織")
    class DeactivateOrganizationTests {

        @Test
        @DisplayName("應成功停用組織")
        void shouldDeactivateOrganization() {
            // Given
            Organization org = Organization.create("ORG001", "名稱", null, null);

            // When
            org.deactivate();

            // Then
            assertEquals(OrganizationStatus.INACTIVE, org.getStatus());
            assertFalse(org.isActive());
        }

        @Test
        @DisplayName("已停用組織再次停用應拋出例外")
        void shouldThrowExceptionWhenAlreadyDeactivated() {
            // Given
            Organization org = Organization.create("ORG001", "名稱", null, null);
            org.deactivate();

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    org::deactivate);
            assertEquals("ALREADY_DEACTIVATED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("重新啟用組織")
    class ActivateOrganizationTests {

        @Test
        @DisplayName("應成功重新啟用組織")
        void shouldActivateOrganization() {
            // Given
            Organization org = Organization.create("ORG001", "名稱", null, null);
            org.deactivate();

            // When
            org.activate();

            // Then
            assertEquals(OrganizationStatus.ACTIVE, org.getStatus());
            assertTrue(org.isActive());
        }

        @Test
        @DisplayName("已啟用組織再次啟用應拋出例外")
        void shouldThrowExceptionWhenAlreadyActive() {
            // Given
            Organization org = Organization.create("ORG001", "名稱", null, null);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    org::activate);
            assertEquals("ALREADY_ACTIVE", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("相等性")
    class EqualityTests {

        @Test
        @DisplayName("相同 ID 的組織應視為相等")
        void shouldBeEqualWhenSameId() {
            // Given
            Organization org1 = Organization.create("ORG001", "名稱1", null, null);

            // 使用 reconstitute 建立相同 ID 的組織
            Organization org2 = Organization.builder()
                    .id(org1.getId())
                    .code("ORG002")
                    .name("名稱2")
                    .status(OrganizationStatus.ACTIVE)
                    .build();

            // Then
            assertEquals(org1, org2);
            assertEquals(org1.hashCode(), org2.hashCode());
        }
    }
}
