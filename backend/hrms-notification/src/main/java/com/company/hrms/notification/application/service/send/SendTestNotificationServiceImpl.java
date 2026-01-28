package com.company.hrms.notification.application.service.send;

import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 測試發送通知 Service
 * <p>
 * 職責：
 * <ol>
 *     <li>測試發送只發送給當前使用者</li>
 *     <li>強制使用 IN_APP 渠道（避免發送真實郵件/推播）</li>
 *     <li>可用於測試範本渲染效果</li>
 * </ol>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Service("sendTestNotificationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SendTestNotificationServiceImpl
        implements CommandApiService<SendNotificationRequest, SendNotificationResponse> {

    private final SendNotificationServiceImpl sendNotificationService;

    @Override
    public SendNotificationResponse execCommand(
            SendNotificationRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        log.info("[SendTestNotification] 測試發送通知 - 使用者: {}, 範本: {}",
                currentUser.getEmployeeNumber(),
                request.getTemplateCode());

        // 建立測試發送請求
        SendNotificationRequest testRequest = SendNotificationRequest.builder()
                // 強制發送給當前使用者
                .recipientId(currentUser.getEmployeeNumber())
                .title("[測試] " + request.getTitle())
                .content(request.getContent())
                .notificationType(request.getNotificationType())
                // 強制只使用 IN_APP 渠道（避免發送真實郵件）
                .channels(List.of("IN_APP"))
                .priority(request.getPriority())
                .templateCode(request.getTemplateCode())
                .templateVariables(request.getTemplateVariables())
                .businessType("TEST")
                .businessId("test-" + System.currentTimeMillis())
                .businessUrl(request.getBusinessUrl())
                .build();

        // 執行發送
        SendNotificationResponse response = sendNotificationService.execCommand(testRequest, currentUser);

        log.info("[SendTestNotification] 測試發送完成 - 通知ID: {}", response.getNotificationId());

        return response;
    }
}
