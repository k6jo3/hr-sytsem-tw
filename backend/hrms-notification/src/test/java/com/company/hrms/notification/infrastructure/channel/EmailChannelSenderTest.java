package com.company.hrms.notification.infrastructure.channel;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;

import jakarta.mail.internet.MimeMessage;

/**
 * EmailChannelSender 單元測試
 * <p>
 * 測試 Email 發送邏輯：啟停控制、收件人查詢、發送流程、錯誤處理
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailChannelSender 單元測試")
class EmailChannelSenderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private OrganizationServiceClient organizationServiceClient;

    @Mock
    private MimeMessage mimeMessage;

    private EmailChannelSender sender;

    @BeforeEach
    void setUp() {
        sender = new EmailChannelSender(mailSender, organizationServiceClient);
        // 設定預設值
        ReflectionTestUtils.setField(sender, "emailEnabled", true);
        ReflectionTestUtils.setField(sender, "fromAddress", "noreply@hrms.local");
    }

    @Nested
    @DisplayName("send - 發送 Email")
    class SendTests {

        @Test
        @DisplayName("Email 通道停用時，應跳過發送且不拋出例外")
        void shouldSkipWhenEmailDisabled() throws Exception {
            // Given
            ReflectionTestUtils.setField(sender, "emailEnabled", false);
            Notification notification = createTestNotification();

            // When
            sender.send(notification, "emp-001");

            // Then - 不應呼叫 mailSender
            verify(mailSender, never()).createMimeMessage();
            verify(mailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("收件人 Email 為空時，應拋出例外")
        void shouldThrowWhenRecipientEmailIsEmpty() {
            // Given
            Notification notification = createTestNotification();
            EmployeeDto employee = new EmployeeDto();
            employee.setEmail(""); // 空 Email
            when(organizationServiceClient.getEmployeeDetail("emp-001")).thenReturn(employee);

            // When & Then - getRecipientEmail 回傳空字串，在 createMimeMessage 之前就會拋出例外
            assertThatThrownBy(() -> sender.send(notification, "emp-001"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Email 發送失敗");
        }

        @Test
        @DisplayName("收件人 Email 為 null 時，應拋出例外")
        void shouldThrowWhenRecipientEmailIsNull() {
            // Given
            Notification notification = createTestNotification();
            EmployeeDto employee = new EmployeeDto();
            employee.setEmail(null);
            when(organizationServiceClient.getEmployeeDetail("emp-001")).thenReturn(employee);

            // When & Then
            assertThatThrownBy(() -> sender.send(notification, "emp-001"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Email 發送失敗");
        }

        @Test
        @DisplayName("員工查詢回傳 null 時，應拋出例外")
        void shouldThrowWhenEmployeeNotFound() {
            // Given
            Notification notification = createTestNotification();
            when(organizationServiceClient.getEmployeeDetail("emp-001")).thenReturn(null);

            // When & Then - getRecipientEmail 回傳 null，在 createMimeMessage 之前就會拋出例外
            assertThatThrownBy(() -> sender.send(notification, "emp-001"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Email 發送失敗");
        }

        @Test
        @DisplayName("員工查詢拋出例外時，應包裝成 Email 發送失敗例外")
        void shouldWrapExceptionWhenOrgServiceFails() {
            // Given
            Notification notification = createTestNotification();
            when(organizationServiceClient.getEmployeeDetail("emp-001"))
                    .thenThrow(new RuntimeException("連線超時"));

            // When & Then - getRecipientEmail 內部 catch 後回傳 null
            assertThatThrownBy(() -> sender.send(notification, "emp-001"))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Email 發送失敗");
        }

        @Test
        @DisplayName("成功發送 Email 時，應正確呼叫 JavaMailSender")
        void shouldCallMailSenderOnSuccess() throws Exception {
            // Given
            Notification notification = createTestNotification();
            EmployeeDto employee = new EmployeeDto();
            employee.setEmail("wang@company.com");
            when(organizationServiceClient.getEmployeeDetail("emp-001")).thenReturn(employee);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            // When
            sender.send(notification, "emp-001");

            // Then
            verify(mailSender).createMimeMessage();
            verify(mailSender).send(mimeMessage);
        }
    }

    @Nested
    @DisplayName("getChannelName - 渠道名稱")
    class GetChannelNameTests {

        @Test
        @DisplayName("應回傳 EMAIL")
        void shouldReturnEmail() {
            assertThat(sender.getChannelName()).isEqualTo("EMAIL");
        }
    }

    // ==================== 輔助方法 ====================

    private Notification createTestNotification() {
        return Notification.create(
                "emp-001",
                "測試通知標題",
                "<p>測試通知內容</p>",
                NotificationType.ANNOUNCEMENT,
                List.of(NotificationChannel.EMAIL),
                NotificationPriority.NORMAL);
    }
}
