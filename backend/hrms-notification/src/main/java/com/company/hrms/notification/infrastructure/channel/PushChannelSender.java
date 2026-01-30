package com.company.hrms.notification.infrastructure.channel;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    private final INotificationPreferenceRepository preferenceRepository;

    // NOTE: 注入 Firebase Messaging 服務 (目前專案尚未加入 firebase-admin 依賴，待整合)
    // private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[PushChannelSender] 發送推播通知 - 收件人: {}", recipientId);

        try {
            // 實作 Firebase FCM 推播
            // 1. 從 DB 查詢收件人的 FCM Token (Multiple tokens for multiple devices)
            List<String> fcmTokens = getRecipientFcmTokens(recipientId);

            if (fcmTokens == null || fcmTokens.isEmpty()) {
                // 不視為錯誤，只是沒有裝置可推播
                log.info("[PushChannelSender] 收件人 {} 未註冊推播裝置，跳過發送", recipientId);
                return;
            }

            for (String fcmToken : fcmTokens) {
                if (fcmToken == null || fcmToken.isBlank())
                    continue;

                // 2. 組裝 FCM Message & 3. 呼叫 Firebase API 發送
                // 目前僅模擬
                log.info("[PushChannelSender] 推播通知發送成功（模擬） - 收件人: {}, FCM Token: {}",
                        recipientId, maskToken(fcmToken));
            }

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
     * 取得收件人的 FCM Tokens
     *
     * @param recipientId 收件人 ID
     * @return FCM Token 列表
     */
    private List<String> getRecipientFcmTokens(String recipientId) {
        return preferenceRepository.findByEmployeeId(recipientId)
                .map(pref -> pref.getPushTokens())
                .orElse(List.of());
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
