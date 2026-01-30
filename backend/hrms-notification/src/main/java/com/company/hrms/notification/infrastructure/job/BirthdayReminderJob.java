package com.company.hrms.notification.infrastructure.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.application.service.send.SendNotificationServiceImpl;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 生日提醒 Job
 * <p>
 * 每日 08:00 發送生日祝福通知
 * </p>
 *
 * @author Claude
 * @since 2026-01-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayReminderJob {

    private final SendNotificationServiceImpl sendNotificationService;
    private final OrganizationServiceClient organizationServiceClient;

    /**
     * 每日 08:00 執行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendBirthdayReminders() {
        log.info("[BirthdayReminderJob] 開始執行生日提醒任務");

        try {
            LocalDate today = LocalDate.now();
            log.info("[BirthdayReminderJob] 正在檢查日期 {} 的生日提醒", today);

            List<EmployeeDto> birthdayEmployees = organizationServiceClient.getEmployeesByBirthday(
                    today.getMonthValue(), today.getDayOfMonth());

            int successCount = 0;
            int failCount = 0;

            for (EmployeeDto employee : birthdayEmployees) {
                try {
                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(employee.getEmployeeId())
                            .notificationType("ANNOUNCEMENT")
                            .priority("NORMAL")
                            .title("🎂 生日快樂！")
                            .content("祝您生日快樂！願您在新的一歲裡工作順利、身體健康！")
                            .channels(List.of("IN_APP", "EMAIL"))
                            .build();

                    // 使用系統帳號發送
                    JWTModel systemUser = createSystemUser();
                    sendNotificationService.execCommand(request, systemUser);

                    successCount++;
                } catch (Exception e) {
                    log.error("[BirthdayReminderJob] 發送生日祝福失敗 - 員工: {}, 錯誤: {}",
                            employee.getEmployeeId(), e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("[BirthdayReminderJob] 生日提醒任務完成 - 成功: {}, 失敗: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("[BirthdayReminderJob] 生日提醒任務執行失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 建立系統使用者（用於 Job 執行）
     */
    private JWTModel createSystemUser() {
        JWTModel systemUser = new JWTModel();
        systemUser.setEmployeeNumber("SYSTEM");
        systemUser.setDisplayName("系統自動通知");
        return systemUser;
    }
}
