package com.company.hrms.notification.infrastructure.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.application.service.send.SendNotificationServiceImpl;

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
    // TODO: 未來應透過 Feign Client 呼叫 Organization Service 取得今日生日員工列表

    /**
     * 每日 08:00 執行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendBirthdayReminders() {
        log.info("[BirthdayReminderJob] 開始執行生日提醒任務");

        try {
            LocalDate today = LocalDate.now();
            log.info("[BirthdayReminderJob] 正在檢查日期 {} 的生日提醒", today);

            // TODO: 查詢今日生日的員工
            // List<Employee> birthdayEmployees =
            // employeeRepository.findByBirthday(today.getMonthValue(),
            // today.getDayOfMonth());

            // 暫時實作：模擬查詢結果
            List<String> birthdayEmployeeIds = List.of(); // 空列表，避免實際發送

            int successCount = 0;
            int failCount = 0;

            for (String employeeId : birthdayEmployeeIds) {
                try {
                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(employeeId)
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
                            employeeId, e.getMessage(), e);
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
