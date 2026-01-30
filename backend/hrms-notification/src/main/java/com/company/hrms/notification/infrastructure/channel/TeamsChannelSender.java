package com.company.hrms.notification.infrastructure.channel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final INotificationPreferenceRepository preferenceRepository;

    @Value("${notification.channel.teams.webhook-url:}")
    private String defaultWebhookUrl;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[TeamsChannelSender] 發送 Teams 通知 - 收件人: {}", recipientId);

        try {
            // 取得 Webhook URL（優先使用收件人設定，否則使用預設值）
            String webhookUrl = getTeamsWebhookUrl(recipientId);

            if (webhookUrl == null || webhookUrl.isBlank()) {
                log.warn("[TeamsChannelSender] 收件人 {} 未設定 Teams Webhook，跳過發送", recipientId);
                return;
            }

            // 組裝 Adaptive Card 訊息
            Map<String, Object> message = buildTeamsMessage(notification);

            // 設定 HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 發送 POST 請求到 Webhook URL
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);

            log.info("[TeamsChannelSender] Teams 通知發送成功 - 收件人: {}, 標題: {}",
                    recipientId, notification.getTitle());

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
     * 取得 Teams Webhook URL
     * <p>
     * 優先順序：收件人個人設定 > 系統預設值
     * </p>
     *
     * @param recipientId 收件人 ID
     * @return Webhook URL
     */
    private String getTeamsWebhookUrl(String recipientId) {
        return preferenceRepository.findByEmployeeId(recipientId)
                .map(pref -> {
                    String url = pref.getTeamsWebhookUrl();
                    return (url != null && !url.isBlank()) ? url : defaultWebhookUrl;
                })
                .orElse(defaultWebhookUrl);
    }

    /**
     * 組裝 Teams 訊息（MessageCard 格式）
     * <p>
     * 使用 Office 365 Connector Card 格式
     * </p>
     *
     * @param notification 通知聚合根
     * @return 訊息 Map 物件
     */
    private Map<String, Object> buildTeamsMessage(Notification notification) {
        Map<String, Object> message = new HashMap<>();
        message.put("@type", "MessageCard");
        message.put("@context", "http://schema.org/extensions");
        message.put("themeColor", getThemeColor(notification));
        message.put("summary", notification.getTitle());
        message.put("title", notification.getTitle());
        message.put("text", notification.getContent());

        // 如果有相關業務連結，加入 potentialAction
        if (notification.getRelatedBusinessUrl() != null && !notification.getRelatedBusinessUrl().isBlank()) {
            Map<String, Object> action = new HashMap<>();
            action.put("@type", "OpenUri");
            action.put("name", "查看詳情");
            action.put("targets", new Object[] {
                    Map.of("os", "default", "uri", notification.getRelatedBusinessUrl())
            });
            message.put("potentialAction", new Object[] { action });
        }

        return message;
    }

    /**
     * 根據通知優先級取得主題顏色
     *
     * @param notification 通知聚合根
     * @return 主題顏色 (hex)
     */
    private String getThemeColor(Notification notification) {
        return switch (notification.getPriority()) {
            case URGENT -> "FF0000"; // 紅色
            case HIGH -> "FFA500"; // 橘色
            case NORMAL -> "0078D7"; // 藍色
            case LOW -> "808080"; // 灰色
        };
    }
}
