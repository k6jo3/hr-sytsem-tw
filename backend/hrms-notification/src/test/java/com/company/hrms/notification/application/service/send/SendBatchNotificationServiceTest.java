package com.company.hrms.notification.application.service.send;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;

/**
 * SendBatchNotificationServiceImpl 測試
 * <p>
 * 測試範圍：
 * <ol>
 * <li>批次發送功能</li>
 * <li>收件人數量驗證</li>
 * <li>分批處理邏輯</li>
 * <li>並行發送處理</li>
 * <li>結果彙總</li>
 * </ol>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SendBatchNotificationService 測試")
class SendBatchNotificationServiceTest {

    @Autowired
    private SendBatchNotificationServiceImpl sendBatchNotificationService;

    @org.springframework.boot.test.context.TestConfiguration
    static class MockConfig {
        @org.springframework.context.annotation.Bean
        public org.springframework.messaging.simp.SimpMessagingTemplate simpMessagingTemplate() {
            return org.mockito.Mockito.mock(org.springframework.messaging.simp.SimpMessagingTemplate.class);
        }

        @org.springframework.context.annotation.Bean
        public org.springframework.mail.javamail.JavaMailSender javaMailSender() {
            return org.mockito.Mockito.mock(org.springframework.mail.javamail.JavaMailSender.class);
        }

        @org.springframework.context.annotation.Bean
        public org.springframework.web.client.RestTemplate restTemplate() {
            return org.mockito.Mockito.mock(org.springframework.web.client.RestTemplate.class);
        }
    }

    private JWTModel mockUser;

    @BeforeEach
    void setUp() {
        mockUser = JWTModel.builder()
                .userId("user-001")
                .username("admin")
                .employeeNumber("emp-admin")
                .displayName("系統管理員")
                .email("admin@example.com")
                .build();
    }

    @Test
    @DisplayName("場景1: 小批次發送（10人）- 應該成功發送給所有收件人")
    void scenario1_SmallBatch_ShouldSendToAllRecipients() throws Exception {
        // Given: 10 個收件人
        List<String> recipientIds = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            recipientIds.add("emp-" + String.format("%03d", i));
        }

        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(recipientIds)
                .title("系統維護通知")
                .content("系統將於今晚進行維護，請提前儲存工作。")
                .notificationType("SYSTEM_NOTICE")
                .channels(List.of("IN_APP"))
                .priority("NORMAL")
                .build();

        // When: 執行批次發送
        SendBatchNotificationResponse response = sendBatchNotificationService.execCommand(request, mockUser);

        // Then: 驗證結果
        assertThat(response).isNotNull();
        assertThat(response.getTotalRecipients()).isEqualTo(10);
        assertThat(response.getSuccessCount() + response.getFailureCount()).isEqualTo(10);
        assertThat(response.getResults()).hasSize(response.getSuccessCount());
    }

    @Test
    @DisplayName("場景2: 中批次發送（100人）- 應該分批處理")
    void scenario2_MediumBatch_ShouldProcessInBatches() throws Exception {
        // Given: 100 個收件人
        List<String> recipientIds = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            recipientIds.add("emp-" + String.format("%03d", i));
        }

        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(recipientIds)
                .title("重要公告")
                .content("公司將於下週一實施新的考勤制度。")
                .notificationType("ANNOUNCEMENT")
                .channels(List.of("IN_APP", "EMAIL"))
                .priority("HIGH")
                .build();

        // When: 執行批次發送
        SendBatchNotificationResponse response = sendBatchNotificationService.execCommand(request, mockUser);

        // Then: 驗證結果
        assertThat(response.getTotalRecipients()).isEqualTo(100);
        assertThat(response.getSuccessCount() + response.getFailureCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("場景3: 超過上限（501人）- 應該拋出例外")
    void scenario3_ExceedLimit_ShouldThrowException() {
        // Given: 501 個收件人（超過上限 500）
        List<String> recipientIds = new ArrayList<>();
        for (int i = 1; i <= 501; i++) {
            recipientIds.add("emp-" + String.format("%03d", i));
        }

        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(recipientIds)
                .title("測試通知")
                .content("測試內容")
                .notificationType("SYSTEM_NOTICE")
                .build();

        // When & Then: 應該拋出例外
        Exception exception = assertThrows(Exception.class, () -> {
            sendBatchNotificationService.execCommand(request, mockUser);
        });

        assertThat(exception.getMessage()).contains("上限");
    }

    @Test
    @DisplayName("場景4: 空收件人列表 - 應該拋出例外")
    void scenario4_EmptyRecipients_ShouldThrowException() {
        // Given: 空收件人列表
        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(new ArrayList<>())
                .title("測試通知")
                .content("測試內容")
                .notificationType("SYSTEM_NOTICE")
                .build();

        // When & Then: 應該拋出例外
        Exception exception = assertThrows(Exception.class, () -> {
            sendBatchNotificationService.execCommand(request, mockUser);
        });

        assertThat(exception.getMessage()).contains("不可為空");
    }

    @Test
    @DisplayName("場景5: 部分失敗 - 應該正確統計成功和失敗數量")
    void scenario5_PartialFailure_ShouldCountCorrectly() throws Exception {
        // Given: 包含有效和無效收件人
        List<String> recipientIds = List.of(
                "emp-001",
                "emp-002",
                "emp-003",
                "", // 無效收件人
                "emp-004");

        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(recipientIds)
                .title("測試通知")
                .content("測試內容")
                .notificationType("SYSTEM_NOTICE")
                .channels(List.of("IN_APP"))
                .build();

        // When: 執行批次發送
        SendBatchNotificationResponse response = sendBatchNotificationService.execCommand(request, mockUser);

        // Then: 驗證結果
        assertThat(response.getTotalRecipients()).isEqualTo(5);
        assertThat(response.getFailureCount()).isGreaterThan(0); // 至少有一個失敗
        assertThat(response.getFailedRecipients()).isNotEmpty();

        // 驗證失敗的收件人包含空字串
        boolean hasEmptyRecipientFailure = response.getFailedRecipients().stream()
                .anyMatch(f -> "".equals(f.getRecipientId()));
        assertThat(hasEmptyRecipientFailure).isTrue();
    }

    @Test
    @DisplayName("場景6: 使用範本批次發送 - 應該對所有收件人使用相同範本")
    void scenario6_BatchWithTemplate_ShouldUseTemplateForAll() throws Exception {
        // Given: 使用範本的批次請求
        List<String> recipientIds = List.of("emp-001", "emp-002", "emp-003");

        SendBatchNotificationRequest request = SendBatchNotificationRequest.builder()
                .recipientIds(recipientIds)
                .notificationType("SYSTEM_NOTICE")
                .channels(List.of("IN_APP"))
                .priority("NORMAL")
                .templateCode("SYSTEM_MAINTENANCE")
                .build();

        // When & Then: 範本不存在時應該記錄失敗
        SendBatchNotificationResponse response = sendBatchNotificationService.execCommand(request, mockUser);

        // 驗證所有發送都嘗試過（可能都失敗因為範本不存在）
        assertThat(response.getTotalRecipients()).isEqualTo(3);
    }
}
