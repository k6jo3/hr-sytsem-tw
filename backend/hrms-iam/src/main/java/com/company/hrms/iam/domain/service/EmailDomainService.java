package com.company.hrms.iam.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 郵件服務
 * 負責發送系統郵件
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDomainService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@company.com}")
    private String fromAddress;

    @Value("${password-reset.base-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    /**
     * 發送密碼重置郵件
     * 
     * @param toEmail     收件者 Email
     * @param resetToken  重置 Token
     * @param displayName 使用者顯示名稱
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken, String displayName) {
        String resetUrl = resetPasswordBaseUrl + "?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("[HRMS] 密碼重置請求");
        message.setText(buildPasswordResetEmailBody(displayName, resetUrl));

        try {
            mailSender.send(message);
            log.info("密碼重置郵件已發送至: {}", toEmail);
        } catch (Exception e) {
            log.error("發送密碼重置郵件失敗: {}", e.getMessage(), e);
            throw new RuntimeException("郵件發送失敗，請稍後再試", e);
        }
    }

    private String buildPasswordResetEmailBody(String displayName, String resetUrl) {
        return String.format(
                """
                        親愛的 %s，

                        您好！

                        我們收到了您的密碼重置請求。請點擊以下連結重置密碼：

                        %s

                        此連結將於 30 分鐘後失效。

                        如果您並未發起此請求，請忽略此郵件。

                        祝好，
                        HRMS 系統管理團隊

                        ---
                        此為系統自動發送郵件，請勿直接回覆。
                        """,
                displayName, resetUrl);
    }
}
