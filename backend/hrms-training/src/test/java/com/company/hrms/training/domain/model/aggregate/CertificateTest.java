package com.company.hrms.training.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;

/**
 * 證照 Aggregate 單元測試
 * 覆蓋建立、驗證規則、過期判斷、更新、驗證操作
 */
class CertificateTest {

    // === 建立證照 ===

    @Nested
    @DisplayName("建立證照")
    class CreateTests {

        @Test
        @DisplayName("建立有效證照 - 無到期日")
        void create_withoutExpiryDate_shouldBeValid() {
            Certificate cert = Certificate.create(
                    "emp-001", "PMP", "PMI", "PMP-12345",
                    LocalDate.now().minusMonths(6), null,
                    CourseCategory.MANAGEMENT, true, null, "備註");

            assertNotNull(cert.getId());
            assertEquals("emp-001", cert.getEmployeeId());
            assertEquals("PMP", cert.getCertificateName());
            assertEquals(CertificateStatus.VALID, cert.getStatus());
            assertFalse(cert.getIsVerified());
            assertNotNull(cert.getCreatedAt());
        }

        @Test
        @DisplayName("建立有效證照 - 有到期日且距離超過 90 天")
        void create_withFarExpiryDate_shouldBeValid() {
            Certificate cert = Certificate.create(
                    "emp-001", "AWS SAA", "Amazon", "AWS-001",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusDays(180),
                    CourseCategory.TECHNICAL, false, null, null);

            assertEquals(CertificateStatus.VALID, cert.getStatus());
        }

        @Test
        @DisplayName("建立證照 - 到期日在 90 天內應為 EXPIRING")
        void create_withNearExpiryDate_shouldBeExpiring() {
            Certificate cert = Certificate.create(
                    "emp-001", "CCNA", "Cisco", "CCNA-001",
                    LocalDate.now().minusMonths(11),
                    LocalDate.now().plusDays(30),
                    CourseCategory.TECHNICAL, false, null, null);

            assertEquals(CertificateStatus.EXPIRING, cert.getStatus());
        }

        @Test
        @DisplayName("建立證照 - 已過期應為 EXPIRED")
        void create_withPastExpiryDate_shouldBeExpired() {
            Certificate cert = Certificate.create(
                    "emp-001", "ITIL", "Axelos", "ITIL-001",
                    LocalDate.now().minusYears(2),
                    LocalDate.now().minusDays(1),
                    CourseCategory.TECHNICAL, false, null, null);

            assertEquals(CertificateStatus.EXPIRED, cert.getStatus());
        }

        @Test
        @DisplayName("建立證照 - isRequired 為 null 時預設 false")
        void create_isRequiredNull_shouldDefaultFalse() {
            Certificate cert = Certificate.create(
                    "emp-001", "CEH", "EC-Council", "CEH-001",
                    LocalDate.now().minusMonths(1), null,
                    CourseCategory.TECHNICAL, null, null, null);

            assertFalse(cert.getIsRequired());
        }

        @Test
        @DisplayName("建立證照 - 應產生 CertificateAddedEvent")
        void create_shouldRegisterEvent() {
            Certificate cert = Certificate.create(
                    "emp-001", "PMP", "PMI", "PMP-001",
                    LocalDate.now().minusMonths(1), null,
                    CourseCategory.MANAGEMENT, true, null, null);

            assertFalse(cert.getDomainEvents().isEmpty());
        }
    }

    // === 驗證規則 ===

    @Nested
    @DisplayName("建立驗證規則")
    class ValidationTests {

        @Test
        @DisplayName("證照名稱為空應拋出例外")
        void create_emptyName_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    Certificate.create("emp-001", "", "PMI", "PMP-001",
                            LocalDate.now(), null, CourseCategory.MANAGEMENT, false, null, null));
        }

        @Test
        @DisplayName("證照名稱為 null 應拋出例外")
        void create_nullName_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    Certificate.create("emp-001", null, "PMI", "PMP-001",
                            LocalDate.now(), null, CourseCategory.MANAGEMENT, false, null, null));
        }

        @Test
        @DisplayName("證照名稱超過 255 字元應拋出例外")
        void create_tooLongName_shouldThrow() {
            String longName = "A".repeat(256);
            assertThrows(IllegalArgumentException.class, () ->
                    Certificate.create("emp-001", longName, "PMI", "PMP-001",
                            LocalDate.now(), null, CourseCategory.MANAGEMENT, false, null, null));
        }

        @Test
        @DisplayName("發證日期為 null 應拋出例外")
        void create_nullIssueDate_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    Certificate.create("emp-001", "PMP", "PMI", "PMP-001",
                            null, null, CourseCategory.MANAGEMENT, false, null, null));
        }

        @Test
        @DisplayName("到期日早於發證日應拋出例外")
        void create_expiryBeforeIssue_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    Certificate.create("emp-001", "PMP", "PMI", "PMP-001",
                            LocalDate.of(2026, 3, 1), LocalDate.of(2026, 2, 1),
                            CourseCategory.MANAGEMENT, false, null, null));
        }
    }

    // === 過期天數 ===

    @Nested
    @DisplayName("過期天數計算")
    class ExpiryTests {

        @Test
        @DisplayName("無到期日 - getDaysUntilExpiry 回傳 Long.MAX_VALUE")
        void getDaysUntilExpiry_noExpiryDate_shouldReturnMaxValue() {
            Certificate cert = Certificate.create(
                    "emp-001", "永久證照", "Org", "C-001",
                    LocalDate.now().minusMonths(1), null,
                    CourseCategory.OTHER, false, null, null);

            assertEquals(Long.MAX_VALUE, cert.getDaysUntilExpiry());
        }

        @Test
        @DisplayName("有到期日 - getDaysUntilExpiry 應回傳正確天數")
        void getDaysUntilExpiry_withExpiryDate_shouldReturnCorrectDays() {
            Certificate cert = Certificate.create(
                    "emp-001", "CCNA", "Cisco", "C-001",
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusDays(100),
                    CourseCategory.TECHNICAL, false, null, null);

            assertEquals(100, cert.getDaysUntilExpiry());
        }
    }

    // === 更新 ===

    @Test
    @DisplayName("更新證照 - 更新到期日後應重新計算狀態")
    void update_changeExpiryDate_shouldRecalculateStatus() {
        Certificate cert = Certificate.create(
                "emp-001", "PMP", "PMI", "PMP-001",
                LocalDate.now().minusYears(1),
                LocalDate.now().plusDays(200),
                CourseCategory.MANAGEMENT, false, null, null);

        assertEquals(CertificateStatus.VALID, cert.getStatus());

        // 更新到期日為 30 天後，應變為 EXPIRING
        cert.update(null, null, LocalDate.now().plusDays(30), null, null);
        assertEquals(CertificateStatus.EXPIRING, cert.getStatus());
    }

    @Test
    @DisplayName("更新證照 - 到期日早於發證日應拋出例外")
    void update_expiryBeforeIssue_shouldThrow() {
        Certificate cert = Certificate.create(
                "emp-001", "PMP", "PMI", "PMP-001",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2027, 6, 1),
                CourseCategory.MANAGEMENT, false, null, null);

        assertThrows(IllegalArgumentException.class, () ->
                cert.update(null, null, LocalDate.of(2026, 1, 1), null, null));
    }

    // === 驗證操作 ===

    @Test
    @DisplayName("驗證證照 - 設定驗證人與時間")
    void verify_shouldSetVerifiedFields() {
        Certificate cert = Certificate.create(
                "emp-001", "PMP", "PMI", "PMP-001",
                LocalDate.now().minusMonths(1), null,
                CourseCategory.MANAGEMENT, true, null, null);

        assertFalse(cert.getIsVerified());

        cert.verify("hr-001");

        assertTrue(cert.getIsVerified());
        assertEquals("hr-001", cert.getVerifiedBy());
        assertNotNull(cert.getVerifiedAt());
    }
}
