package com.company.hrms.notification.infrastructure.channel;

import org.springframework.beans.factory.annotation.Value;
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
 * 透過 Spring Mail（{@link JavaMailSender}）發送 Email 通知。
 * 可透過 {@code hrms.notification.channel.email.enabled} 屬性控制是否啟用，
 * 預設為 {@code true}。設為 {@code false} 時，呼叫 {@link #send} 僅記錄日誌、不實際發送。
 * </p>
 * <p>
 * 發送流程：
 * <ol>
 *   <li>檢查 Email 通道是否啟用</li>
 *   <li>透過 {@link OrganizationServiceClient} 查詢收件人的 Email 地址</li>
 *   <li>使用 {@link MimeMessageHelper} 組裝 MIME 郵件（支援 HTML 內容）</li>
 *   <li>透過 {@link JavaMailSender} 發送至 SMTP 伺服器</li>
 * </ol>
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

    /**
     * 是否啟用 Email 通道，預設為 {@code true}。
     * 設為 {@code false} 時，呼叫 {@link #send} 僅記錄日誌、不實際發送。
     */
    @Value("${hrms.notification.channel.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * 寄件人地址，從 {@code hrms.notification.channel.email.from} 取得，
     * 預設為 {@code noreply@hrms.local}
     */
    @Value("${hrms.notification.channel.email.from:noreply@hrms.local}")
    private String fromAddress;

    @Override
    public void send(Notification notification, String recipientId) throws Exception {
        // 檢查 Email 通道是否啟用
        if (!emailEnabled) {
            log.info("[EmailChannelSender] Email 通道已停用，跳過發送 - 收件人: {}, 標題: {}",
                    recipientId, notification.getTitle());
            return;
        }

        log.debug("[EmailChannelSender] 發送 Email 通知 - 收件人: {}", recipientId);

        try {
            // 從員工服務查詢收件人的 Email
            String recipientEmail = getRecipientEmail(recipientId);

            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("收件人 Email 為空，無法發送: " + recipientId);
            }

            // 建立 MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getContent(), true); // true = 以 HTML 格式發送

            // 發送
            mailSender.send(message);

            log.info("[EmailChannelSender] Email 發送成功 - 收件人: {}, Email: {}, 寄件人: {}",
                    recipientId, recipientEmail, fromAddress);

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
     * 呼叫員工服務 API 取得員工詳細資訊，從中擷取 Email 地址。
     * 若呼叫失敗則回傳 null，由呼叫端決定後續處理。
     * </p>
     *
     * @param recipientId 收件人 ID (Employee ID)
     * @return Email 地址；查詢失敗時回傳 null
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
