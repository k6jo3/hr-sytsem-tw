package com.company.hrms.notification.infrastructure.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LINE 通知發送器
 * <p>
 * 透過 LINE Notify API 發送通知
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LineChannelSender implements ChannelSender {

    private static final String LINE_NOTIFY_API = "https://notify-api.line.me/api/notify";

    private final RestTemplate restTemplate;
    private final INotificationPreferenceRepository preferenceRepository;

    @Value("${notification.channel.line.default-token:}")
    private String defaultLineToken;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[LineChannelSender] 發送 LINE 通知 - 收件人: {}", recipientId);

        try {
            // 取得收件人的 LINE Token（優先使用個人設定，否則使用預設值）
            String lineToken = getRecipientLineToken(recipientId);

            if (lineToken == null || lineToken.isBlank()) {
                log.warn("[LineChannelSender] 收件人 {} 未綁定 LINE 帳號，跳過發送", recipientId);
                return;
            }

            // 組裝訊息
            String message = buildLineMessage(notification);

            // 設定 HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(lineToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 組裝 POST 請求內容
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("message", message);

            // 發送 POST 請求到 LINE Notify API
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(LINE_NOTIFY_API, request, String.class);

            log.info("[LineChannelSender] LINE 通知發送成功 - 收件人: {}, 標題: {}",
                    recipientId, notification.getTitle());

        } catch (Exception e) {
            log.error("[LineChannelSender] LINE 通知發送失敗 - 收件人: {}, 錯誤: {}",
                    recipientId, e.getMessage(), e);
            throw new Exception("LINE 通知發送失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public String getChannelName() {
        return "LINE";
    }

    /**
     * 取得收件人的 LINE Token
     * <p>
     * 優先順序：收件人個人綁定 Token (目前使用 LineUserId 欄位替代) > 系統預設 Token
     * </p>
     *
     * @param recipientId 收件人 ID
     * @return LINE Token
     */
    private String getRecipientLineToken(String recipientId) {
        return preferenceRepository.findByEmployeeId(recipientId)
                .map(pref -> {
                    String token = pref.getLineUserId(); // 注意：目前假設 LineUserId 欄位存放 Token
                    return (token != null && !token.isBlank()) ? token : defaultLineToken;
                })
                .orElse(defaultLineToken);
    }

    /**
     * 組裝 LINE 訊息
     * <p>
     * 格式化通知內容為 LINE 文字訊息
     * </p>
     *
     * @param notification 通知聚合根
     * @return 訊息文字
     */
    private String buildLineMessage(Notification notification) {
        StringBuilder sb = new StringBuilder();

        // 根據優先級加入表情符號
        String emoji = switch (notification.getPriority()) {
            case URGENT -> "🚨";
            case HIGH -> "⚠️";
            case NORMAL -> "📢";
            case LOW -> "ℹ️";
        };

        sb.append("\n").append(emoji).append(" ").append(notification.getTitle()).append("\n\n");
        sb.append(notification.getContent());

        // 如果有相關業務連結，加入連結
        if (notification.getRelatedBusinessUrl() != null && !notification.getRelatedBusinessUrl().isBlank()) {
            sb.append("\n\n").append("🔗 詳情: ").append(notification.getRelatedBusinessUrl());
        }

        return sb.toString();
    }
}
