package com.company.hrms.notification.infrastructure.channel;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final OrganizationServiceClient organizationServiceClient;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        log.debug("[EmailChannelSender] 發送 Email 通知 - 收件人: {}", recipientId);

        try {
            // 建立 MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 從員工服務查詢收件人的 Email
            String recipientEmail = getRecipientEmail(recipientId);

            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("收件人 Email 為空，無法發送: " + recipientId);
            }

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
     * 取得收件人 Email
     * <p>
     * 呼叫員工服務 API 取得員工詳細資訊
     * </p>
     *
     * @param recipientId 收件人 ID (Employee ID)
     * @return Email 地址
     */
    private String getRecipientEmail(String recipientId) {
        try {
            EmployeeDto employee = organizationServiceClient.getEmployeeDetail(recipientId);
            if (employee != null) {
                return employee.getEmail();
            }
        } catch (Exception e) {
            log.error("查詢員工資料失敗: recipientId={}, error={}", recipientId, e.getMessage());
        }
        return null;
    }
}
