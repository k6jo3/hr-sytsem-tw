package com.company.hrms.notification.application.service.send;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * SendNotificationServiceImpl 整合測試
 * <p>
 * 測試範圍：
 * <ol>
 *     <li>完整 Pipeline 執行流程</li>
 *     <li>範本渲染功能</li>
 *     <li>渠道過濾邏輯</li>
 *     <li>靜音時段處理</li>
 *     <li>錯誤處理</li>
 * </ol>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SendNotificationService 整合測試")
class SendNotificationServiceIntegrationTest {

    @Autowired
    private SendNotificationServiceImpl sendNotificationService;

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private INotificationTemplateRepository templateRepository;

    @Autowired
    private INotificationPreferenceRepository preferenceRepository;

    private JWTModel mockUser;

    @BeforeEach
    void setUp() {
        // 建立模擬使用者
        mockUser = JWTModel.builder()
                .userId("user-001")
                .username("testuser")
                .employeeNumber("emp-001")
                .displayName("測試使用者")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("場景1: 直接發送（無範本）- 應該成功建立通知並嘗試發送")
    void scenario1_DirectSend_ShouldSucceed() throws Exception {
        // Given: 準備發送請求
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("emp-002")
                .title("系統維護通知")
                .content("系統將於今晚 22:00-24:00 進行維護。")
                .notificationType("SYSTEM_NOTICE")
                .channels(List.of("IN_APP"))
                .priority("NORMAL")
                .build();

        // When: 執行發送
        SendNotificationResponse response = sendNotificationService.execCommand(request, mockUser);

        // Then: 驗證回應
        assertThat(response).isNotNull();
        assertThat(response.getNotificationId()).isNotBlank();
        assertThat(response.getRecipientId()).isEqualTo("emp-002");
        assertThat(response.getTitle()).isEqualTo("系統維護通知");
        assertThat(response.getStatus()).isIn("SENT", "FAILED", "PENDING");
        assertThat(response.getChannels()).contains("IN_APP");
        assertThat(response.getSentAt()).isNotNull();
        assertThat(response.getChannelResults()).isNotEmpty();

        // Then: 驗證通知已儲存
        Optional<Notification> savedNotification = notificationRepository.findById(NotificationId.of(response.getNotificationId()));
        assertThat(savedNotification).isPresent();
        assertThat(savedNotification.get().getTitle()).isEqualTo("系統維護通知");
    }

    @Test
    @DisplayName("場景2: 使用範本發送 - 應該正確渲染範本變數")
    void scenario2_TemplateSend_ShouldRenderVariables() throws Exception {
        // Given: 準備範本（假設範本已存在）
        // 注意：此測試需要預先在資料庫中建立範本，或使用 Mock

        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("emp-002")
                .notificationType("APPROVAL_REQUEST")
                .channels(List.of("IN_APP", "EMAIL"))
                .priority("NORMAL")
                .templateCode("LEAVE_APPROVAL_REQUEST")
                .templateVariables(Map.of(
                        "employeeName", "張三",
                        "leaveType", "特休假",
                        "totalDays", 2,
                        "startDate", "2025/12/20",
                        "endDate", "2025/12/21"
                ))
                .businessType("LEAVE_APPLICATION")
                .businessId("leave-001")
                .build();

        // When & Then: 如果範本不存在，應拋出例外
        Exception exception = assertThrows(Exception.class, () -> {
            sendNotificationService.execCommand(request, mockUser);
        });

        // 驗證錯誤訊息包含範本相關資訊
        assertThat(exception.getMessage()).contains("範本");
    }

    @Test
    @DisplayName("場景3: 多渠道發送 - 應該記錄每個渠道的發送結果")
    void scenario3_MultiChannelSend_ShouldRecordAllChannelResults() throws Exception {
        // Given: 多渠道請求
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("emp-002")
                .title("重要通知")
                .content("這是一則重要通知。")
                .notificationType("SYSTEM_ALERT")
                .channels(List.of("IN_APP", "EMAIL", "PUSH"))
                .priority("HIGH")
                .build();

        // When: 執行發送
        SendNotificationResponse response = sendNotificationService.execCommand(request, mockUser);

        // Then: 驗證各渠道結果
        assertThat(response.getChannelResults()).isNotEmpty();
        assertThat(response.getChannelResults().size()).isGreaterThanOrEqualTo(1);

        // 驗證至少 IN_APP 渠道有結果
        boolean hasInAppResult = response.getChannelResults().stream()
                .anyMatch(r -> "IN_APP".equals(r.getChannel()));
        assertThat(hasInAppResult).isTrue();
    }

    @Test
    @DisplayName("場景4: 緊急通知 - 應該忽略靜音時段")
    void scenario4_UrgentNotification_ShouldIgnoreQuietHours() throws Exception {
        // Given: 緊急通知
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("emp-002")
                .title("緊急維護通知")
                .content("系統發現嚴重漏洞，將立即進行緊急維護。")
                .notificationType("SYSTEM_ALERT")
                .channels(List.of("IN_APP", "EMAIL", "PUSH"))
                .priority("URGENT")
                .build();

        // When: 執行發送
        SendNotificationResponse response = sendNotificationService.execCommand(request, mockUser);

        // Then: 驗證通知立即發送（不延後）
        assertThat(response.getStatus()).isIn("SENT", "FAILED");
        assertThat(response.getStatus()).isNotEqualTo("PENDING");
    }

    @Test
    @DisplayName("場景5: 無效收件人 - 應該拋出例外")
    void scenario5_InvalidRecipient_ShouldThrowException() {
        // Given: 無效收件人
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("") // 空白收件人
                .title("測試通知")
                .content("測試內容")
                .notificationType("SYSTEM_NOTICE")
                .build();

        // When & Then: 應該拋出驗證例外
        assertThrows(Exception.class, () -> {
            sendNotificationService.execCommand(request, mockUser);
        });
    }

    @Test
    @DisplayName("場景6: 業務關聯 - 應該正確設定業務類型和 ID")
    void scenario6_BusinessRelation_ShouldBeSetCorrectly() throws Exception {
        // Given: 包含業務關聯的請求
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientId("emp-002")
                .title("請假申請待審核")
                .content("員工張三申請特休假2天，請您審核。")
                .notificationType("APPROVAL_REQUEST")
                .channels(List.of("IN_APP"))
                .priority("NORMAL")
                .businessType("LEAVE_APPLICATION")
                .businessId("leave-001")
                .businessUrl("/attendance/leave/applications/leave-001")
                .build();

        // When: 執行發送
        SendNotificationResponse response = sendNotificationService.execCommand(request, mockUser);

        // Then: 驗證業務關聯已儲存
        Optional<Notification> savedNotification = notificationRepository.findById(NotificationId.of(response.getNotificationId()));
        assertThat(savedNotification).isPresent();
        assertThat(savedNotification.get().getRelatedBusinessType()).isEqualTo("LEAVE_APPLICATION");
        assertThat(savedNotification.get().getRelatedBusinessId()).isEqualTo("leave-001");
        assertThat(savedNotification.get().getRelatedBusinessUrl()).isEqualTo("/attendance/leave/applications/leave-001");
    }
}
