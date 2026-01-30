package com.company.hrms.notification.infrastructure.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.application.service.send.SendNotificationServiceImpl;
import com.company.hrms.notification.infrastructure.client.training.TrainingServiceClient;
import com.company.hrms.notification.infrastructure.client.training.dto.CertificateExpiryDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 證照到期提醒 Job
 * <p>
 * 每週一 09:00 檢查並提醒即將到期的證照（提前 60 天、30 天、7 天）
 * </p>
 *
 * @author Claude
 * @since 2026-01-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateExpiryJob {

    private final SendNotificationServiceImpl sendNotificationService;
    private final TrainingServiceClient trainingServiceClient;

    /**
     * 每週一 09:00 執行
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendCertificateExpiryReminders() {
        log.info("[CertificateExpiryJob] 開始執行證照到期提醒任務");

        try {
            // 檢查三個時間點：60天後、30天後、7天後
            sendRemindersForDaysAhead(60, "即將到期");
            sendRemindersForDaysAhead(30, "即將到期");
            sendRemindersForDaysAhead(7, "即將到期");

            log.info("[CertificateExpiryJob] 證照到期提醒任務完成");

        } catch (Exception e) {
            log.error("[CertificateExpiryJob] 證照到期提醒任務執行失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 發送指定天數後到期的提醒
     */
    private void sendRemindersForDaysAhead(int daysAhead, String urgencyLabel) {
        try {
            LocalDate expiryDate = LocalDate.now().plusDays(daysAhead);
            log.info("[CertificateExpiryJob] 檢查於 {} 到期的證照", expiryDate);

            // 查詢範圍: 到期日當天 (簡化邏輯，實際可擴大範圍)
            List<CertificateExpiryDto> expiringCertificates = trainingServiceClient.getExpiringCertificates(
                    expiryDate.toString(), expiryDate.toString());

            for (CertificateExpiryDto cert : expiringCertificates) {
                try {
                    String priority = daysAhead <= 30 ? "HIGH" : "NORMAL";

                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(cert.getEmployeeId())
                            .notificationType("REMINDER")
                            .priority(priority)
                            .title(String.format("📜 證照%s提醒", urgencyLabel))
                            .content(String.format("您的「%s」證照將於 %d 天後（%s）到期，請儘速辦理展延或換證。",
                                    cert.getCertificateName(), daysAhead, cert.getExpiryDate()))
                            .channels(List.of("IN_APP", "EMAIL"))
                            .businessUrl("/hr/certificates/" + cert.getCertificateId())
                            .build();

                    sendNotificationService.execCommand(request, createSystemUser());

                } catch (Exception e) {
                    log.error("[CertificateExpiryJob] 發送證照到期提醒失敗 - 員工: {}, 證照: {}, 錯誤: {}",
                            cert.getEmployeeId(), cert.getCertificateName(), e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("[CertificateExpiryJob] 處理 {} 天後到期證照失敗: {}", daysAhead, e.getMessage(), e);
        }
    }

    private JWTModel createSystemUser() {
        JWTModel systemUser = new JWTModel();
        systemUser.setEmployeeNumber("SYSTEM");
        systemUser.setDisplayName("系統自動通知");
        return systemUser;
    }
}
