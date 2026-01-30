package com.company.hrms.notification.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.model.valueobject.NotificationStatus;
import com.company.hrms.notification.domain.repository.INotificationRepository;

/**
 * Notification Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據收件人查詢</li>
 * <li>未讀通知查詢</li>
 * <li>未讀數量統計</li>
 * <li>範本狀態存在性檢查</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/notification_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Notification Repository 整合測試")
class NotificationRepositoryIntegrationTest {

    @Autowired
    private INotificationRepository notificationRepository;

    // 測試用固定 ID
    private static final String NTF_01 = "ntf-00000000-0001";
    private static final String NTF_05 = "ntf-00000000-0005";
    private static final String RECIPIENT_E001 = "E001";
    private static final String RECIPIENT_E002 = "E002";
    private static final String RECIPIENT_E003 = "E003";

    // ========================================================================
    // 1. 根據 ID 查詢測試
    // ========================================================================
    @Nested
    @DisplayName("1. 根據 ID 查詢測試")
    class FindByIdTests {

        @Test
        @DisplayName("findById - 存在的通知應返回正確資料")
        void findById_ExistingNotification_ShouldReturnNotification() {
            // Given
            NotificationId notificationId = NotificationId.of(NTF_01);

            // When
            Optional<Notification> result = notificationRepository.findById(notificationId);

            // Then
            assertThat(result)
                    .as("應找到通知")
                    .isPresent();
            assertThat(result.get().getTitle())
                    .as("標題應為 請假申請已核准")
                    .isEqualTo("請假申請已核准");
            assertThat(result.get().getStatus())
                    .as("狀態應為 READ")
                    .isEqualTo(NotificationStatus.READ);
        }

        @Test
        @DisplayName("findById - 待發送通知應返回正確資料")
        void findById_PendingNotification_ShouldReturnNotification() {
            // Given
            NotificationId notificationId = NotificationId.of(NTF_05);

            // When
            Optional<Notification> result = notificationRepository.findById(notificationId);

            // Then
            assertThat(result)
                    .as("應找到通知")
                    .isPresent();
            assertThat(result.get().getStatus())
                    .as("狀態應為 PENDING")
                    .isEqualTo(NotificationStatus.PENDING);
        }

        @Test
        @DisplayName("findById - 不存在的通知應返回空")
        void findById_NonExisting_ShouldReturnEmpty() {
            // Given
            NotificationId notificationId = NotificationId.of("ntf-99999999-9999");

            // When
            Optional<Notification> result = notificationRepository.findById(notificationId);

            // Then
            assertThat(result)
                    .as("不存在的通知應返回空")
                    .isEmpty();
        }
    }

    // ========================================================================
    // 2. 收件人查詢測試
    // ========================================================================
    @Nested
    @DisplayName("2. 收件人查詢測試")
    class RecipientQueryTests {

        @Test
        @DisplayName("findByRecipientId - E001 的通知")
        void findByRecipientId_E001_ShouldReturnNotifications() {
            // When
            List<Notification> result = notificationRepository.findByRecipientId(RECIPIENT_E001);

            // Then
            assertThat(result)
                    .as("E001 應有 5 筆通知")
                    .hasSize(5);
        }

        @Test
        @DisplayName("findByRecipientId - E002 的通知")
        void findByRecipientId_E002_ShouldReturnNotifications() {
            // When
            List<Notification> result = notificationRepository.findByRecipientId(RECIPIENT_E002);

            // Then
            assertThat(result)
                    .as("E002 應有 4 筆通知")
                    .hasSize(4);
        }

        @Test
        @DisplayName("findByRecipientId - E003 的通知")
        void findByRecipientId_E003_ShouldReturnNotifications() {
            // When
            List<Notification> result = notificationRepository.findByRecipientId(RECIPIENT_E003);

            // Then
            assertThat(result)
                    .as("E003 應有 3 筆通知")
                    .hasSize(3);
        }

        @Test
        @DisplayName("findByRecipientId - 不存在的收件人應返回空列表")
        void findByRecipientId_NonExisting_ShouldReturnEmpty() {
            // When
            List<Notification> result = notificationRepository.findByRecipientId("E999");

            // Then
            assertThat(result)
                    .as("不存在的收件人應返回空列表")
                    .isEmpty();
        }
    }

    // ========================================================================
    // 3. 未讀通知查詢測試
    // ========================================================================
    @Nested
    @DisplayName("3. 未讀通知查詢測試")
    class UnreadQueryTests {

        @Test
        @DisplayName("findUnreadByRecipientId - E001 的未讀通知")
        void findUnreadByRecipientId_E001_ShouldReturnUnreadNotifications() {
            // When
            List<Notification> result = notificationRepository.findUnreadByRecipientId(RECIPIENT_E001);

            // Then
            // E001: 3 已讀 + 2 未讀 (PENDING + SENT)
            assertThat(result)
                    .as("E001 應有 2 筆未讀通知")
                    .hasSize(2)
                    .allMatch(n -> n.getStatus() == NotificationStatus.PENDING ||
                            n.getStatus() == NotificationStatus.SENT);
        }

        @Test
        @DisplayName("findUnreadByRecipientId - E002 的未讀通知")
        void findUnreadByRecipientId_E002_ShouldReturnUnreadNotifications() {
            // When
            List<Notification> result = notificationRepository.findUnreadByRecipientId(RECIPIENT_E002);

            // Then
            // E002: 1 已讀 + 3 未讀 (SENT + PENDING + FAILED)
            // 注意: FAILED 不算未讀，所以應該是 2 筆 (SENT + PENDING)
            assertThat(result)
                    .as("E002 應有 2 筆未讀通知 (不含 FAILED)")
                    .hasSize(2)
                    .allMatch(n -> n.getStatus() == NotificationStatus.PENDING ||
                            n.getStatus() == NotificationStatus.SENT);
        }

        @Test
        @DisplayName("findUnreadByRecipientId - E003 的未讀通知")
        void findUnreadByRecipientId_E003_ShouldReturnUnreadNotifications() {
            // When
            List<Notification> result = notificationRepository.findUnreadByRecipientId(RECIPIENT_E003);

            // Then
            // E003: 1 已讀 + 1 SENT + 1 FAILED
            assertThat(result)
                    .as("E003 應有 1 筆未讀通知 (不含 FAILED)")
                    .hasSize(1)
                    .allMatch(n -> n.getStatus() == NotificationStatus.SENT);
        }
    }

    // ========================================================================
    // 4. 未讀數量統計測試
    // ========================================================================
    @Nested
    @DisplayName("4. 未讀數量統計測試")
    class UnreadCountTests {

        @Test
        @DisplayName("countUnreadByRecipientId - E001 的未讀數量")
        void countUnreadByRecipientId_E001_ShouldReturn2() {
            // When
            long count = notificationRepository.countUnreadByRecipientId(RECIPIENT_E001);

            // Then
            assertThat(count)
                    .as("E001 應有 2 筆未讀通知")
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("countUnreadByRecipientId - E002 的未讀數量")
        void countUnreadByRecipientId_E002_ShouldReturn2() {
            // When
            long count = notificationRepository.countUnreadByRecipientId(RECIPIENT_E002);

            // Then
            assertThat(count)
                    .as("E002 應有 2 筆未讀通知 (不含 FAILED)")
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("countUnreadByRecipientId - E003 的未讀數量")
        void countUnreadByRecipientId_E003_ShouldReturn1() {
            // When
            long count = notificationRepository.countUnreadByRecipientId(RECIPIENT_E003);

            // Then
            assertThat(count)
                    .as("E003 應有 1 筆未讀通知 (不含 FAILED)")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("countUnreadByRecipientId - 不存在的收件人應返回 0")
        void countUnreadByRecipientId_NonExisting_ShouldReturn0() {
            // When
            long count = notificationRepository.countUnreadByRecipientId("E999");

            // Then
            assertThat(count)
                    .as("不存在的收件人應返回 0")
                    .isEqualTo(0);
        }
    }

    // ========================================================================
    // 5. 範本狀態存在性檢查測試
    // ========================================================================
    @Nested
    @DisplayName("5. 範本狀態存在性檢查測試")
    class TemplateStatusExistsTests {

        @Test
        @DisplayName("existsByTemplateCodeAndStatus - LEAVE_APPROVED + READ 應存在")
        void existsByTemplateCodeAndStatus_LeaveApprovedRead_ShouldReturnTrue() {
            // When
            boolean exists = notificationRepository.existsByTemplateCodeAndStatus(
                    "LEAVE_APPROVED", NotificationStatus.READ);

            // Then
            assertThat(exists)
                    .as("LEAVE_APPROVED 範本有 READ 狀態的通知")
                    .isTrue();
        }

        @Test
        @DisplayName("existsByTemplateCodeAndStatus - ANNOUNCEMENT + PENDING 應存在")
        void existsByTemplateCodeAndStatus_AnnouncementPending_ShouldReturnTrue() {
            // When
            boolean exists = notificationRepository.existsByTemplateCodeAndStatus(
                    "ANNOUNCEMENT", NotificationStatus.PENDING);

            // Then
            assertThat(exists)
                    .as("ANNOUNCEMENT 範本有 PENDING 狀態的通知")
                    .isTrue();
        }

        @Test
        @DisplayName("existsByTemplateCodeAndStatus - ANNOUNCEMENT + FAILED 應存在")
        void existsByTemplateCodeAndStatus_AnnouncementFailed_ShouldReturnTrue() {
            // When
            boolean exists = notificationRepository.existsByTemplateCodeAndStatus(
                    "ANNOUNCEMENT", NotificationStatus.FAILED);

            // Then
            assertThat(exists)
                    .as("ANNOUNCEMENT 範本有 FAILED 狀態的通知")
                    .isTrue();
        }

        @Test
        @DisplayName("existsByTemplateCodeAndStatus - 不存在的組合應返回 false")
        void existsByTemplateCodeAndStatus_NonExisting_ShouldReturnFalse() {
            // When
            boolean exists = notificationRepository.existsByTemplateCodeAndStatus(
                    "NON_EXISTING_TEMPLATE", NotificationStatus.READ);

            // Then
            assertThat(exists)
                    .as("不存在的範本代碼應返回 false")
                    .isFalse();
        }
    }

    // ========================================================================
    // 6. 狀態統計測試
    // ========================================================================
    @Nested
    @DisplayName("6. 狀態統計測試")
    class StatusStatisticsTests {

        @Test
        @DisplayName("各收件人通知數量應正確")
        void recipientStatistics_ShouldBeCorrect() {
            // When
            List<Notification> e001 = notificationRepository.findByRecipientId(RECIPIENT_E001);
            List<Notification> e002 = notificationRepository.findByRecipientId(RECIPIENT_E002);
            List<Notification> e003 = notificationRepository.findByRecipientId(RECIPIENT_E003);

            // Then
            assertThat(e001.size()).as("E001 通知數").isEqualTo(5);
            assertThat(e002.size()).as("E002 通知數").isEqualTo(4);
            assertThat(e003.size()).as("E003 通知數").isEqualTo(3);

            // 總和應為 12
            int total = e001.size() + e002.size() + e003.size();
            assertThat(total).as("總通知數").isEqualTo(12);
        }
    }
}
