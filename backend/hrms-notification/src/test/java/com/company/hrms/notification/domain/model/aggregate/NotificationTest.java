package com.company.hrms.notification.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationStatus;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;

/**
 * Notification 聚合根單元測試
 *
 * @author Claude
 * @since 2026-01-29
 */
@DisplayName("Notification 聚合根測試")
class NotificationTest {

    @Nested
    @DisplayName("建立通知測試")
    class CreateNotificationTests {

        @Test
        @DisplayName("應該成功建立通知")
        void shouldCreateNotificationSuccessfully() {
            // Given
            String recipientId = "EMP001";
            String title = "測試通知";
            String content = "這是測試內容";
            NotificationType type = NotificationType.ANNOUNCEMENT;
            List<NotificationChannel> channels = List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL);
            NotificationPriority priority = NotificationPriority.NORMAL;

            // When
            Notification notification = Notification.create(
                    recipientId, title, content, type, channels, priority);

            // Then
            assertThat(notification).isNotNull();
            assertThat(notification.getId()).isNotNull();
            assertThat(notification.getRecipientId()).isEqualTo(recipientId);
            assertThat(notification.getTitle()).isEqualTo(title);
            assertThat(notification.getContent()).isEqualTo(content);
            assertThat(notification.getNotificationType()).isEqualTo(type);
            assertThat(notification.getChannels()).containsExactlyElementsOf(channels);
            assertThat(notification.getPriority()).isEqualTo(priority);
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
            assertThat(notification.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("應該拋出例外當收件人 ID 為 null")
        void shouldThrowExceptionWhenRecipientIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> Notification.create(
                    null, "標題", "內容", NotificationType.ANNOUNCEMENT,
                    List.of(NotificationChannel.IN_APP), NotificationPriority.NORMAL))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("收件人 ID");
        }

        @Test
        @DisplayName("應該拋出例外當標題為空")
        void shouldThrowExceptionWhenTitleIsBlank() {
            // When & Then
            assertThatThrownBy(() -> Notification.create(
                    "EMP001", "", "內容", NotificationType.ANNOUNCEMENT,
                    List.of(NotificationChannel.IN_APP), NotificationPriority.NORMAL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("標題");
        }

        @Test
        @DisplayName("當渠道列表為空時應該使用預設渠道 IN_APP")
        void shouldUseDefaultChannelWhenChannelsIsEmpty() {
            // When
            Notification notification = Notification.create(
                    "EMP001", "標題", "內容", NotificationType.ANNOUNCEMENT,
                    List.of(), NotificationPriority.NORMAL);

            // Then
            assertThat(notification.getChannels()).containsExactly(NotificationChannel.IN_APP);
        }
    }

    @Nested
    @DisplayName("狀態轉換測試")
    class StatusTransitionTests {

        @Test
        @DisplayName("應該成功標記為已發送")
        void shouldMarkAsSentSuccessfully() {
            // Given
            Notification notification = createTestNotification();
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);

            // When
            notification.markAsSent();

            // Then
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
            assertThat(notification.getSentAt()).isNotNull();
            assertThat(notification.getSentAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("應該成功標記為失敗")
        void shouldMarkAsFailedSuccessfully() {
            // Given
            Notification notification = createTestNotification();
            String errorMessage = "發送失敗：網路錯誤";

            // When
            notification.markAsFailed(errorMessage);

            // Then
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
            assertThat(notification.getFailureReason()).isEqualTo(errorMessage);
        }

        @Test
        @DisplayName("應該成功標記為已讀")
        void shouldMarkAsReadSuccessfully() {
            // Given
            Notification notification = createTestNotification();
            notification.markAsSent();

            // When
            notification.markAsRead();

            // Then
            assertThat(notification.getReadAt()).isNotNull();
            assertThat(notification.getReadAt()).isAfterOrEqualTo(notification.getSentAt());
            assertThat(notification.isUnread()).isFalse();
        }

        @Test
        @DisplayName("應該拋出例外當嘗試標記未發送的通知為已讀")
        void shouldThrowExceptionWhenMarkingPendingNotificationAsRead() {
            // Given
            Notification notification = createTestNotification();

            // When & Then
            assertThatThrownBy(notification::markAsRead)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("已發送");
        }
    }

    @Nested
    @DisplayName("業務關聯測試")
    class BusinessRelationTests {

        @Test
        @DisplayName("應該成功設定業務關聯")
        void shouldSetBusinessRelationSuccessfully() {
            // Given
            Notification notification = createTestNotification();
            String businessType = "LEAVE_APPLICATION";
            String businessId = "LEAVE-2024-001";

            // When
            notification.setBusinessRelation(businessType, businessId);

            // Then
            assertThat(notification.getRelatedBusinessType()).isEqualTo(businessType);
            assertThat(notification.getRelatedBusinessId()).isEqualTo(businessId);
        }

        @Test
        @DisplayName("應該成功設定業務 URL")
        void shouldSetBusinessUrlSuccessfully() {
            // Given
            Notification notification = createTestNotification();
            String businessUrl = "/hr/leaves/LEAVE-2024-001";

            // When
            notification.setBusinessUrl(businessUrl);

            // Then
            assertThat(notification.getRelatedBusinessUrl()).isEqualTo(businessUrl);
        }
    }

    @Nested
    @DisplayName("狀態查詢測試")
    class StatusQueryTests {

        @Test
        @DisplayName("新建立的通知狀態為 PENDING，不是未讀狀態")
        void newNotificationShouldBePending() {
            // Given
            Notification notification = createTestNotification();

            // Then
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
            assertThat(notification.isUnread()).isFalse(); // PENDING 不算未讀
        }

        @Test
        @DisplayName("已發送的通知應該返回 true")
        void sentNotificationShouldReturnTrue() {
            // Given
            Notification notification = createTestNotification();
            notification.markAsSent();

            // Then
            assertThat(notification.isSent()).isTrue();
        }

        @Test
        @DisplayName("失敗的通知應該返回 true")
        void failedNotificationShouldReturnTrue() {
            // Given
            Notification notification = createTestNotification();
            notification.markAsFailed("測試錯誤");

            // Then
            assertThat(notification.isFailed()).isTrue();
        }

        @Test
        @DisplayName("已讀的通知應該不是未讀狀態")
        void readNotificationShouldNotBeUnread() {
            // Given
            Notification notification = createTestNotification();
            notification.markAsSent();
            notification.markAsRead();

            // Then
            assertThat(notification.isUnread()).isFalse();
        }
    }

    // ==================== Helper Methods ====================

    private Notification createTestNotification() {
        return Notification.create(
                "EMP001",
                "測試通知",
                "這是測試內容",
                NotificationType.ANNOUNCEMENT,
                List.of(NotificationChannel.IN_APP),
                NotificationPriority.NORMAL);
    }
}
