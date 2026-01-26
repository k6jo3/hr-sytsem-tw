package com.company.hrms.notification.infrastructure.channel;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    private final RestTemplate restTemplate;

    private static final String LINE_NOTIFY_API = "https://notify-api.line.me/api/notify";

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[LineChannelSender] 發送 LINE 通知 - 收件人: {}", recipientId);

        try {
            // TODO: 實作 LINE Notify API
            // 1. 從資料庫取得收件人的 LINE Token
            // 2. 組裝 POST 請求
            // 3. 呼叫 LINE Notify API

            String lineToken = getRecipientLineToken(recipientId);

            if (lineToken == null || lineToken.isBlank()) {
                throw new Exception("收件人未綁定 LINE 帳號");
            }

            // 組裝訊息
            String message = buildLineMessage(notification);

            // 發送（暫時註解，避免編譯錯誤）
            // HttpHeaders headers = new HttpHeaders();
            // headers.setBearerAuth(lineToken);
            // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            // body.add("message", message);
            // HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            // restTemplate.postForEntity(LINE_NOTIFY_API, request, String.class);

            log.info("[LineChannelSender] LINE 通知發送成功（暫時實作） - 收件人: {}",
                    recipientId);

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
     * 取得收件人的 LINE Token（暫時實作）
     * TODO: 整合資料庫查詢
     *
     * @param recipientId 收件人 ID
     * @return LINE Token
     */
    private String getRecipientLineToken(String recipientId) {
        // 暫時實作：回傳空值（表示未綁定）
        return null;
    }

    /**
     * 組裝 LINE 訊息
     *
     * @param notification 通知聚合根
     * @return 訊息文字
     */
    private String buildLineMessage(Notification notification) {
        StringBuilder sb = new StringBuilder();
        sb.append("📢 ").append(notification.getTitle()).append("\n\n");
        sb.append(notification.getContent());

        if (notification.getRelatedBusinessUrl() != null) {
            sb.append("\n\n").append("詳情: ").append(notification.getRelatedBusinessUrl());
        }

        return sb.toString();
    }
}
