package com.company.hrms.notification.infrastructure.channel;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

/**
 * Email 通知發送器
 * <p>
 * 透過 Spring Mail 發送 Email 通知
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannelSender implements ChannelSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[EmailChannelSender] 發送 Email 通知 - 收件人: {}", recipientId);

        try {
            // 建立 MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // TODO: 從員工服務查詢收件人的 Email
            String recipientEmail = getRecipientEmail(recipientId);

            helper.setTo(recipientEmail);
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getContent(), true); // true = HTML format

            // 發送
            mailSender.send(message);

            log.info("[EmailChannelSender] Email 發送成功 - 收件人: {}, Email: {}",
                    recipientId, recipientEmail);

        } catch (Exception e) {
            log.error("[EmailChannelSender] Email 發送失敗 - 收件人: {}, 錯誤: {}",
                    recipientId, e.getMessage(), e);
            throw new Exception("Email 發送失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public String getChannelName() {
        return "EMAIL";
    }

    /**
     * 取得收件人 Email（暫時實作）
     * TODO: 整合員工服務 API
     *
     * @param recipientId 收件人 ID
     * @return Email 地址
     */
    private String getRecipientEmail(String recipientId) {
        // 暫時實作：使用收件人 ID + @company.com
        return recipientId + "@company.com";
    }
}
