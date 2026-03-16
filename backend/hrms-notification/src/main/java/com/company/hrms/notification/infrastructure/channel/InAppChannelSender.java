package com.company.hrms.notification.infrastructure.channel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.company.hrms.notification.domain.model.aggregate.Notification;

import lombok.extern.slf4j.Slf4j;

/**
 * 站內通知發送器
 * <p>
 * 透過 WebSocket (STOMP) 發送即時站內通知
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
public class InAppChannelSender implements ChannelSender {

    private final SimpMessagingTemplate messagingTemplate;

    public InAppChannelSender(@Qualifier("brokerMessagingTemplate") SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[InAppChannelSender] 發送站內通知 - 收件人: {}", recipientId);

        try {
            // 組裝 WebSocket 訊息
            var message = buildMessage(notification);

            // 發送到使用者的訂閱端點
            String destination = "/user/" + recipientId + "/queue/notifications";
            messagingTemplate.convertAndSend(destination, message);

            log.info("[InAppChannelSender] 站內通知發送成功 - 收件人: {}, 目的地: {}",
                    recipientId, destination);

        } catch (Exception e) {
            log.error("[InAppChannelSender] 站內通知發送失敗 - 收件人: {}, 錯誤: {}",
                    recipientId, e.getMessage(), e);
            throw new Exception("站內通知發送失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public String getChannelName() {
        return "IN_APP";
    }

    /**
     * 組裝 WebSocket 訊息
     *
     * @param notification 通知聚合根
     * @return 訊息物件
     */
    private Object buildMessage(Notification notification) {
        return new InAppMessage(
                notification.getId().getValue(),
                notification.getTitle(),
                notification.getContent(),
                notification.getNotificationType().name(),
                notification.getPriority().name(),
                notification.getRelatedBusinessType(),
                notification.getRelatedBusinessId(),
                notification.getRelatedBusinessUrl(),
                notification.getCreatedAt());
    }

    /**
     * WebSocket 訊息物件
     */
    private record InAppMessage(
            String notificationId,
            String title,
            String content,
            String notificationType,
            String priority,
            String businessType,
            String businessId,
            String businessUrl,
            java.time.LocalDateTime createdAt) {
    }
}
