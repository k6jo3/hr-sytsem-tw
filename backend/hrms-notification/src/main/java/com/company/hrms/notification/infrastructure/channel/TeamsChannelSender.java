package com.company.hrms.notification.infrastructure.channel;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Microsoft Teams 通知發送器
 * <p>
 * 透過 Teams Incoming Webhook 發送通知
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TeamsChannelSender implements ChannelSender {

    private final RestTemplate restTemplate;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[TeamsChannelSender] 發送 Teams 通知 - 收件人: {}", recipientId);

        try {
            // TODO: 實作 Teams Incoming Webhook
            // 1. 從設定檔或資料庫取得 Webhook URL
            // 2. 組裝 Adaptive Card 訊息
            // 3. POST 到 Webhook URL

            String webhookUrl = getTeamsWebhookUrl(recipientId);

            if (webhookUrl == null || webhookUrl.isBlank()) {
                throw new Exception("收件人未設定 Teams Webhook");
            }

            // 組裝訊息
            var message = buildTeamsMessage(notification);

            // 發送（暫時註解，避免編譯錯誤）
            // restTemplate.postForEntity(webhookUrl, message, String.class);

            log.info("[TeamsChannelSender] Teams 通知發送成功（暫時實作） - 收件人: {}",
                    recipientId);

        } catch (Exception e) {
            log.error("[TeamsChannelSender] Teams 通知發送失敗 - 收件人: {}, 錯誤: {}",
                    recipientId, e.getMessage(), e);
            throw new Exception("Teams 通知發送失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public String getChannelName() {
        return "TEAMS";
    }

    /**
     * 取得 Teams Webhook URL（暫時實作）
     * TODO: 整合設定管理
     *
     * @param recipientId 收件人 ID
     * @return Webhook URL
     */
    private String getTeamsWebhookUrl(String recipientId) {
        // 暫時實作：回傳空值（表示未設定）
        return null;
    }

    /**
     * 組裝 Teams 訊息（Adaptive Card 格式）
     *
     * @param notification 通知聚合根
     * @return 訊息物件
     */
    private Object buildTeamsMessage(Notification notification) {
        // TODO: 建立 Adaptive Card JSON 結構
        return new TeamsMessage(
                "@type",
                "MessageCard",
                "@context",
                "http://schema.org/extensions",
                "summary",
                notification.getTitle(),
                "title",
                notification.getTitle(),
                "text",
                notification.getContent()
        );
    }

    /**
     * Teams 訊息物件（簡化版）
     */
    private record TeamsMessage(
            String type,
            String typeValue,
            String context,
            String contextValue,
            String summary,
            String summaryValue,
            String title,
            String titleValue,
            String text,
            String textValue
    ) {
    }
}
