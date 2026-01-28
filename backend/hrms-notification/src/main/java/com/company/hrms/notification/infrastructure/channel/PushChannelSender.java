package com.company.hrms.notification.infrastructure.channel;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 推播通知發送器
 * <p>
 * 透過 Firebase FCM 發送推播通知
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushChannelSender implements ChannelSender {

    // TODO: 注入 Firebase Messaging 服務
    // private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[PushChannelSender] 發送推播通知 - 收件人: {}", recipientId);

        try {
            // TODO: 實作 Firebase FCM 推播
            // 1. 從 Redis/DB 查詢收件人的 FCM Token
            // 2. 組裝 FCM Message
            // 3. 呼叫 Firebase API 發送

            String fcmToken = getRecipientFcmToken(recipientId);

            if (fcmToken == null || fcmToken.isBlank()) {
                throw new Exception("收件人未註冊推播裝置");
            }

            // 暫時實作：只記錄日誌
            log.info("[PushChannelSender] 推播通知發送成功（暫時實作） - 收件人: {}, FCM Token: {}",
                    recipientId, maskToken(fcmToken));

        } catch (Exception e) {
            log.error("[PushChannelSender] 推播通知發送失敗 - 收件人: {}, 錯誤: {}",
                    recipientId, e.getMessage(), e);
            throw new Exception("推播通知發送失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public String getChannelName() {
        return "PUSH";
    }

    /**
     * 取得收件人的 FCM Token（暫時實作）
     * TODO: 整合 Redis 或資料庫查詢
     *
     * @param recipientId 收件人 ID
     * @return FCM Token
     */
    private String getRecipientFcmToken(String recipientId) {
        // 暫時實作：回傳假 Token
        return "fcm_token_" + recipientId;
    }

    /**
     * 遮罩 Token（安全性考量）
     *
     * @param token FCM Token
     * @return 遮罩後的 Token
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }
}
